package utils.undo;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import utils.SegmentVector;
import utils.circuit.Curve;
import utils.circuit.Straight;

/**
 * The state of everything reachable from an object, kept so it can be put back
 * later into the same objects. Putting it back changes the objects themselves
 * rather than replacing them, so undo steps that refer to an object (an edit, a
 * delete, a move) still find it. Objects deleted since are kept by the snapshot
 * and come back.
 *
 * It follows the editor's own classes (utils...), collections, maps, arrays and
 * points. The track's segments are left out: they have their own undo steps.
 * Anything else (strings, numbers, images, files) is kept as it is.
 */
public class Snapshot
{
	private static final Map<Class<?>, Field[]>	fieldCache	= new HashMap<Class<?>, Field[]>();

	// object -> its state: field values, elements, entries or a copy
	private final IdentityHashMap<Object, Object>	states		= new IdentityHashMap<Object, Object>();

	public Snapshot(Object root)
	{
		List<Object> todo = new ArrayList<Object>();
		todo.add(root);

		while (!todo.isEmpty())
		{
			Object object = todo.remove(todo.size() - 1);

			if (object == null || !isFollowed(object.getClass()) || states.containsKey(object))
				continue;

			Object state = capture(object);
			states.put(object, state);
			addReferences(state, todo);
		}
	}

	/**
	 * @return whether the objects are in the same state in both snapshots
	 */
	public boolean sameAs(Snapshot other)
	{
		if (states.size() != other.states.size())
			return false;

		for (Map.Entry<Object, Object> entry : states.entrySet())
		{
			if (!other.states.containsKey(entry.getKey()))
				return false;
			if (!sameState(entry.getValue(), other.states.get(entry.getKey())))
				return false;
		}
		return true;
	}

	/**
	 * Puts the state back into the objects.
	 */
	public void restore()
	{
		// fields first: a set or map may hash its elements by their values
		for (Map.Entry<Object, Object> entry : states.entrySet())
		{
			Object object = entry.getKey();

			if (!(object instanceof Collection) && !(object instanceof Map))
				restore(object, entry.getValue());
		}

		for (Map.Entry<Object, Object> entry : states.entrySet())
		{
			Object object = entry.getKey();

			if (object instanceof Collection || object instanceof Map)
				restore(object, entry.getValue());
		}
	}

	/**
	 * @return whether objects of this class are followed, rather than kept as
	 *         they are
	 */
	private static boolean isFollowed(Class<?> c)
	{
		if (c.isArray())
			return true;

		// the track's segments have their own undo steps
		if (SegmentVector.class.isAssignableFrom(c) || Straight.class.isAssignableFrom(c) || Curve.class.isAssignableFrom(c))
			return false;

		if (Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c))
			return true;

		if (Point2D.class.isAssignableFrom(c) || RectangularShape.class.isAssignableFrom(c))
			return true;

		return isEditorClass(c);
	}

	/** @return whether it's one of the editor's own data classes */
	private static boolean isEditorClass(Class<?> c)
	{
		return c.getName().startsWith("utils.") && !c.getName().startsWith("utils.undo.")
			&& c != utils.Properties.class && !Component.class.isAssignableFrom(c) && !c.isEnum();
	}

	private static Object capture(Object object)
	{
		Class<?> c = object.getClass();

		if (c.isArray())
		{
			int length = Array.getLength(object);
			Object copy = Array.newInstance(c.getComponentType(), length);
			System.arraycopy(object, 0, copy, 0, length);
			return copy;
		}

		if (object instanceof Collection)
		{
			// a model class can also be a collection: keep its fields too
			return new CollectionState(new ArrayList<Object>((Collection<?>) object), fieldValues(object));
		}

		if (object instanceof Map)
		{
			return new MapState(new ArrayList<Map.Entry<?, ?>>(copyEntries((Map<?, ?>) object)), fieldValues(object));
		}

		return new ObjectState(geometry(object), fieldValues(object));
	}

	private static List<Map.Entry<?, ?>> copyEntries(Map<?, ?> map)
	{
		List<Map.Entry<?, ?>> entries = new ArrayList<Map.Entry<?, ?>>();

		for (Map.Entry<?, ?> entry : map.entrySet())
			entries.add(new java.util.AbstractMap.SimpleEntry<Object, Object>(entry.getKey(), entry.getValue()));

		return entries;
	}

	/** @return a copy of a point or rectangle's coordinates, or null */
	private static Object geometry(Object object)
	{
		if (object instanceof Point2D)
			return new Point2D.Double(((Point2D) object).getX(), ((Point2D) object).getY());

		if (object instanceof RectangularShape)
		{
			RectangularShape shape = (RectangularShape) object;
			return new java.awt.geom.Rectangle2D.Double(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
		}

		return null;
	}

	/** @return the values of the fields the editor's own classes declare */
	private static Object[] fieldValues(Object object)
	{
		Field[] fields = fields(object.getClass());
		Object[] values = new Object[fields.length];

		try
		{
			for (int i = 0; i < fields.length; i++)
				values[i] = fields[i].get(object);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalStateException(e);
		}
		return values;
	}

	/** @return the instance fields declared by the editor's classes in its hierarchy */
	private static synchronized Field[] fields(Class<?> c)
	{
		Field[] fields = fieldCache.get(c);

		if (fields == null)
		{
			List<Field> list = new ArrayList<Field>();

			for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass())
			{
				if (!isEditorClass(k))
					continue;

				for (Field field : k.getDeclaredFields())
				{
					if (Modifier.isStatic(field.getModifiers()))
						continue;
					field.setAccessible(true);
					list.add(field);
				}
			}
			fields = list.toArray(new Field[list.size()]);
			fieldCache.put(c, fields);
		}
		return fields;
	}

	/** Adds the objects a state refers to. */
	private static void addReferences(Object state, List<Object> todo)
	{
		if (state.getClass().isArray())
		{
			if (!state.getClass().getComponentType().isPrimitive())
			{
				for (Object element : (Object[]) state)
					todo.add(element);
			}
			return;
		}

		for (Object value : ((State) state).fields)
			todo.add(value);

		if (state instanceof CollectionState)
		{
			todo.addAll(((CollectionState) state).elements);
		}
		else if (state instanceof MapState)
		{
			for (Map.Entry<?, ?> entry : ((MapState) state).entries)
			{
				todo.add(entry.getKey());
				todo.add(entry.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void restore(Object object, Object state)
	{
		if (object.getClass().isArray())
		{
			System.arraycopy(state, 0, object, 0, Array.getLength(state));
			return;
		}

		State s = (State) state;
		Field[] fields = fields(object.getClass());

		try
		{
			for (int i = 0; i < fields.length; i++)
				fields[i].set(object, s.fields[i]);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalStateException(e);
		}

		try
		{
			if (s instanceof CollectionState)
			{
				Collection<Object> collection = (Collection<Object>) object;
				collection.clear();
				collection.addAll(((CollectionState) s).elements);
			}
			else if (s instanceof MapState)
			{
				Map<Object, Object> map = (Map<Object, Object>) object;
				map.clear();
				for (Map.Entry<?, ?> entry : ((MapState) s).entries)
					map.put(entry.getKey(), entry.getValue());
			}
		}
		catch (UnsupportedOperationException e)
		{
			// one that can't be changed hasn't been changed
		}

		Object geometry = s instanceof ObjectState ? ((ObjectState) s).geometry : null;

		if (geometry instanceof Point2D)
		{
			((Point2D) object).setLocation((Point2D) geometry);
		}
		else if (geometry instanceof RectangularShape)
		{
			RectangularShape r = (RectangularShape) geometry;
			((RectangularShape) object).setFrame(r.getX(), r.getY(), r.getWidth(), r.getHeight());
		}
	}

	private boolean sameState(Object a, Object b)
	{
		if (a.getClass().isArray())
		{
			if (!b.getClass().isArray() || Array.getLength(a) != Array.getLength(b))
				return false;
			if (a.getClass().getComponentType().isPrimitive())
				return Objects.deepEquals(a, b);
			Object[] x = (Object[]) a;
			Object[] y = (Object[]) b;
			for (int i = 0; i < x.length; i++)
			{
				if (!sameValue(x[i], y[i]))
					return false;
			}
			return true;
		}

		if (a.getClass() != b.getClass())
			return false;

		State x = (State) a;
		State y = (State) b;

		if (!sameValues(x.fields, y.fields))
			return false;

		if (x instanceof ObjectState)
			return Objects.equals(((ObjectState) x).geometry, ((ObjectState) y).geometry);

		if (x instanceof CollectionState)
			return sameValues(((CollectionState) x).elements.toArray(), ((CollectionState) y).elements.toArray());

		List<Map.Entry<?, ?>> e = ((MapState) x).entries;
		List<Map.Entry<?, ?>> f = ((MapState) y).entries;
		if (e.size() != f.size())
			return false;
		for (int i = 0; i < e.size(); i++)
		{
			if (!sameValue(e.get(i).getKey(), f.get(i).getKey()) || !sameValue(e.get(i).getValue(), f.get(i).getValue()))
				return false;
		}
		return true;
	}

	private boolean sameValues(Object[] a, Object[] b)
	{
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++)
		{
			if (!sameValue(a[i], b[i]))
				return false;
		}
		return true;
	}

	/** Followed objects are the same object; values are equal. */
	private boolean sameValue(Object a, Object b)
	{
		if (a == b)
			return true;
		if (a == null || b == null || states.containsKey(a) || states.containsKey(b))
			return false;
		return a.equals(b);
	}

	private static class State
	{
		final Object[]	fields;

		State(Object[] fields)
		{
			this.fields = fields;
		}
	}

	private static class ObjectState extends State
	{
		final Object	geometry;

		ObjectState(Object geometry, Object[] fields)
		{
			super(fields);
			this.geometry = geometry;
		}
	}

	private static class CollectionState extends State
	{
		final List<Object>	elements;

		CollectionState(List<Object> elements, Object[] fields)
		{
			super(fields);
			this.elements = elements;
		}
	}

	private static class MapState extends State
	{
		final List<Map.Entry<?, ?>>	entries;

		MapState(List<Map.Entry<?, ?>> entries, Object[] fields)
		{
			super(fields);
			this.entries = entries;
		}
	}
}
