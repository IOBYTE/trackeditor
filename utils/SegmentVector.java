package utils;

import java.io.PrintStream;
import java.util.Vector;

import utils.circuit.Segment;

public class SegmentVector extends Vector<Segment>
{
	public Segment getSegmentFromName(String name)
	{
		for (int i = 0; i < size(); i++)
		{
			Segment segment = get(i);

			if (segment.getName() != null && segment.getName().equals(name))
			{
				return segment;
			}
		}

		return null;
	}

	public synchronized Segment set(int index, Segment segment)
	{
		Segment current = get(index);

		segment.nextShape = current.nextShape;
		segment.previousShape = current.previousShape;

		current.set(segment);

		return current;
	}

    public synchronized boolean add(Segment segment)
    {
    	Segment last = null;

    	if (size() > 0)
    	{
    		last = lastElement();
   
    		last.nextShape = segment;
    	}
  
    	segment.previousShape = last;
   
    	return super.add(segment);
    }

    public synchronized void insertElementAt(Segment segment, int index)
    {
    	Segment current = null;
    	Segment previous = null;

    	if (index < size())
    	{
    		current = elementAt(index);
    		previous = current.previousShape;
    	}
    	else if (size() != 0)
    	{
    		previous = lastElement();
    	}

    	super.insertElementAt(segment,  index);
    	
    	if (previous != null)
    	{
    		previous.nextShape = segment;
    		segment.previousShape = previous;
    	}

    	segment.nextShape = current;

    	if (current != null)
    	{
    		current.previousShape = segment;
    	}
    }

    public synchronized Segment remove(int index)
    {
    	Segment current = elementAt(index);
    	Segment next = current.nextShape;
    	Segment previous = current.previousShape;

    	if (next != null)
    	{
    		next.previousShape = previous;
    	}

    	if (previous != null)
    	{
    		previous.nextShape = next;
    	}

    	current.nextShape = null;
    	current.previousShape = null;

    	return super.remove(index);
    }

    public synchronized void removeElementAt(int index)
    {
    	Segment current = elementAt(index);
    	Segment next = current.nextShape;
    	Segment previous = current.previousShape;

    	if (next != null)
    	{
    		next.previousShape = previous;
    	}

    	if (previous != null)
    	{
    		previous.nextShape = next;
    	}

    	current.nextShape = null;
    	current.previousShape = null;

    	super.removeElementAt(index);
    }

	public void dump(String indent)
    {
		System.out.println(indent + "Track Segments");

		for (int i = 0; i < size(); i++)
		{
			System.out.println(indent + "  segment[" + i + "]");
			get(i).dump(indent + "    ");
		}
    }

	public void dumpCalculated(String indent)
	{
		System.out.println(indent + "Track Segments");

		for (int i = 0; i < size(); i++)
		{
			System.out.println(indent + "  segment[" + i + "]");
			get(i).dump(indent + "    ", true, false, false, false);
		}
	}

	public void dump(PrintStream printStream)
	{
		for (int i = 0; i < size(); i++)
		{
			printStream.println("segment[" + i + "]");
			get(i).dump(printStream, "    ", true, false, false, false);
		}
	}

	public void dumpLinks()
	{
		for (int i = 0; i < size(); i++)
		{
			System.out.println("segment[" + i + "] " +
					(get(i).previousShape != null ? get(i).previousShape.getName() : "null") +
					" <- " + get(i).getName() + " -> " + 
					(get(i).nextShape != null ? get(i).nextShape.getName() : "null"));
		}
	}
	
	public boolean validateLinks()
	{
		boolean valid = true;
		if (size() > 0)
		{
			if (get(0).getPreviousShape() != null)
			{
				System.out.println("segment[0] previousShape not null");
				valid = false;
			}

			if (get(size() - 1).getNextShape() != null)
			{
				System.out.println("segment[" + (size() - 1) + "] nextShape not null");
				valid = false;
			}
		}

		for (int i = 0; i < size(); i++)
		{
			Segment segment = get(i);

			if (segment.previousShape == null && i != 0)
			{
				System.out.println("segment[" + i + "] previousShape null");
				valid = false;
			}
			
			if (segment.previousShape != null && segment.previousShape.nextShape != null &&
					!segment.previousShape.nextShape.getName().equals(segment.getName()))
			{
				System.out.println("segment[" + i + "] previousShape bad");
				valid = false;
			}
			
			if (segment.nextShape == null && i != size() - 1)
			{
				System.out.println("segment[" + i + "] nextShape null");
				valid = false;
			}
			
			if (segment.nextShape != null && segment.nextShape.previousShape != null &&
					!segment.nextShape.previousShape.getName().equals(segment.getName()))
			{
				System.out.println("segment[" + i + "] nextShape bad");
				valid = false;
			}
		}
			
		return valid;
	}

	public Segment getSegment(String name)
	{
		for (Segment segment : this)
		{
			if (segment.getName().equals(name))
			{
				return segment;
			}
		}
		return null;
	}
}
