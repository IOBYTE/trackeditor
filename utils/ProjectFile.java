package utils;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import utils.circuit.Pits;

/**
 * Reads a project file (.prj.xml). XMLDecoder makes whatever objects and
 * calls whatever methods the file asks for, so a project file someone else
 * sent could run any code. The file is checked first and only decoded if all
 * it does is set the properties of the classes a project is saved with.
 *
 * Properties the editor no longer has are still allowed: XMLDecoder reports
 * them and goes on, which is how old projects open.
 */
public class ProjectFile
{
	/** The classes XMLEncoder writes for a project */
	private static final Set<Class<?>>	CLASSES	= new HashSet<Class<?>>(Arrays.<Class<?>>asList(
		Properties.class, EditorPoint.class, Pits.Generator.class, Pits.GeneratorPart.class));

	/** The elements that hold a value: they make no object of their own choosing */
	private static final Set<String>	VALUES	= new HashSet<String>(Arrays.asList(
		"string", "boolean", "byte", "short", "int", "long", "float", "double", "char", "null"));

	private ProjectFile()
	{
	}

	/**
	 * @param file the project file
	 * @return the properties the file holds
	 * @throws IOException if the file can't be read, isn't a project file or
	 *         does more than set properties
	 */
	public static Properties read(File file) throws IOException
	{
		// check and decode the same bytes, so the file can't change in between
		byte[] content = Files.readAllBytes(file.toPath());
		check(content);

		Object object;
		try (XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(content)))
		{
			// a property the editor no longer has (author, category, track name, the
			// pits: they are in the track's file now) is left out quietly; anything
			// else is reported as XMLDecoder does
			decoder.setExceptionListener(e ->
			{
				if (e instanceof NoSuchMethodException && String.valueOf(e.getMessage()).contains("Properties.set"))
					return;
				System.err.println(e);
				System.err.println("Continuing ...");
			});
			object = decoder.readObject();
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new IOException("The file holds no project.");
		}
		if (!(object instanceof Properties))
			throw new IOException("The file holds no project.");
		return (Properties) object;
	}

	/**
	 * @throws IOException if the content isn't a project file or does more
	 *         than set properties
	 */
	private static void check(byte[] content) throws IOException
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			// no DTD: it could read other files or expand entities without end
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(content), new Checker());
		}
		catch (SAXParseException e)
		{
			throw new IOException("Not a project file: " + e.getMessage() + " (line " + e.getLineNumber() + ")");
		}
		catch (SAXException e)
		{
			throw new IOException("Not a project file: " + e.getMessage());
		}
		catch (ParserConfigurationException e)
		{
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Follows which object each element works on, so a property can only be
	 * set or got on one of the project classes.
	 */
	private static class Checker extends DefaultHandler
	{
		/** For each open element, the class of the object it works on, or null for a value */
		private final Deque<Element>	open	= new ArrayDeque<Element>();
		/** The class of the object each id names, or null for a value */
		private final Map<String, Class<?>>	ids	= new HashMap<String, Class<?>>();

		private static class Element
		{
			final String	name;
			final Class<?>	type;

			Element(String name, Class<?> type)
			{
				this.name = name;
				this.type = type;
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			Element parent = open.peek();
			Class<?> type = null;

			if (parent == null)
			{
				if (!qName.equals("java"))
					throw new SAXException("it starts with <" + qName + "> instead of <java>");
				allow(qName, attributes, "version", "class");
				String owner = attributes.getValue("class");
				if (owner != null && !owner.equals(XMLDecoder.class.getName()))
					throw new SAXException("<java class=\"" + owner + "\"> isn't allowed");
			}
			else if (parent.name.equals("string"))
			{
				// a string holds characters XML can't, as <char code="..."/>
				if (!qName.equals("char"))
					throw new SAXException("<" + qName + "> isn't allowed in a <string>");
				allow(qName, attributes, "code");
			}
			else if (VALUES.contains(parent.name))
			{
				throw new SAXException("<" + qName + "> isn't allowed in a <" + parent.name + ">");
			}
			else if (qName.equals("object"))
			{
				allow(qName, attributes, "class", "id", "idref");
				String className = attributes.getValue("class");
				String idref = attributes.getValue("idref");
				if (className != null)
				{
					type = projectClass(className);
					if (type == null)
						throw new SAXException("objects of class " + className + " aren't allowed");
				}
				else if (idref != null)
				{
					type = reference(idref);
				}
				else
				{
					throw new SAXException("<object> without a class");
				}
			}
			else if (qName.equals("void"))
			{
				allow(qName, attributes, "property", "id", "idref", "method");
				String property = attributes.getValue("property");
				String idref = attributes.getValue("idref");
				String method = attributes.getValue("method");
				if (property != null)
				{
					if (parent.type == null)
						throw new SAXException("property " + property + " isn't on a project object");
					type = propertyClass(parent.type, property);
				}
				else if (idref != null)
				{
					type = reference(idref);
				}
				else if (method != null)
				{
					// XMLEncoder writes a second setting with the same value, like two
					// booleans that are false, as a reference to the value an earlier
					// setting's getter returned: <void id="Boolean0" method="isRoadOnly"/>.
					// Only a project object's getter, which reads a setting, is allowed.
					if (parent.type == null || attributes.getValue("id") == null)
						throw new SAXException("method " + method + " isn't allowed here");
					type = getterClass(parent.type, method);
					if (type == null)
						throw new SAXException("method " + method + " isn't a project setting's getter");
				}
				else
				{
					throw new SAXException("<void> without a property");
				}
			}
			else if (VALUES.contains(qName))
			{
				if (qName.equals("char"))
					allow(qName, attributes, "id", "code");
				else
					allow(qName, attributes, "id");
			}
			else
			{
				throw new SAXException("<" + qName + "> isn't allowed");
			}

			String id = attributes.getValue("id");
			if (id != null)
				ids.put(id, type);
			open.push(new Element(qName, type));
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			open.pop();
		}

		/** @throws SAXException if the element has an attribute that isn't one of these */
		private static void allow(String element, Attributes attributes, String... names) throws SAXException
		{
			for (int i = 0; i < attributes.getLength(); i++)
			{
				if (!Arrays.asList(names).contains(attributes.getQName(i)))
					throw new SAXException("<" + element + " " + attributes.getQName(i) + "=\"...\"> isn't allowed");
			}
		}

		/** @return the class an id names, null for a value */
		private Class<?> reference(String idref) throws SAXException
		{
			if (!ids.containsKey(idref))
				throw new SAXException("unknown id " + idref);
			return ids.get(idref);
		}

		/** @return the project class with this name, null for any other */
		private static Class<?> projectClass(String className)
		{
			for (Class<?> c : CLASSES)
			{
				if (c.getName().equals(className))
					return c;
			}
			return null;
		}

		/**
		 * @return the class of a property when it is one of the project
		 *         classes, null otherwise: its value can be set but nothing
		 *         can be done with it
		 */
		/** @return the type a project setting's getter of this name returns, null if it isn't one */
		private static Class<?> getterClass(Class<?> type, String method)
		{
			try
			{
				for (PropertyDescriptor descriptor : Introspector.getBeanInfo(type).getPropertyDescriptors())
				{
					if (descriptor.getReadMethod() != null && descriptor.getReadMethod().getName().equals(method)
						&& descriptor.getWriteMethod() != null)
					{
						// a value, or one of the project's objects
						Class<?> c = descriptor.getPropertyType();
						if (c.isPrimitive() || c == String.class || CLASSES.contains(c))
							return c;
					}
				}
			}
			catch (IntrospectionException e)
			{
			}
			return null;
		}

		private static Class<?> propertyClass(Class<?> type, String property)
		{
			try
			{
				for (PropertyDescriptor descriptor : Introspector.getBeanInfo(type).getPropertyDescriptors())
				{
					if (descriptor.getName().equals(property) && CLASSES.contains(descriptor.getPropertyType()))
						return descriptor.getPropertyType();
				}
			}
			catch (IntrospectionException e)
			{
			}
			return null;
		}
	}
}
