/*
 *   Subject.java
 *
 *    This file is part of the track editor.
 *
 *    The track editor is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    The track editor is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 */
package utils.check;

import java.util.Arrays;

import utils.circuit.Segment;

/**
 * What a check message is about, as data: the editor shows it when the message is
 * double-clicked (see CheckDialog), and a command line checker can print it.
 */
public final class Subject
{
	public enum Kind
	{
		/** About nothing that can be shown */
		NONE,
		/** A segment, in the track view and the segment dialog, on a tab or as it is */
		SEGMENT,
		/** A tab of the Properties dialog, and in it the tab with the next title and so on */
		PROPERTIES,
		/** The closure: Calc in the Delta's dialog, for figures there are none of yet */
		CALCULATE_CLOSURE,
		/** The closure: the Delta's dialog with the figures of the last trackgen run */
		LAST_CLOSURE
	}

	/** For a message about nothing that can be shown, when the messages around it are */
	public static final Subject	NONE	= new Subject(Kind.NONE, null, null, new String[0]);

	private final Kind			kind;
	private final Segment		segment;
	private final String		segmentTab;
	private final String[]		tabs;

	private Subject(Kind kind, Segment segment, String segmentTab, String[] tabs)
	{
		this.kind = kind;
		this.segment = segment;
		this.segmentTab = segmentTab;
		this.tabs = tabs;
	}

	/**
	 * @param tab Left or Right for a side, null for the segment dialog as it is
	 * @return the segment, or null when there is none
	 */
	public static Subject segment(Segment segment, String tab)
	{
		return segment == null ? null : new Subject(Kind.SEGMENT, segment, tab, new String[0]);
	}

	/** @return a tab of the Properties dialog, and in it the tab with the next title and so on */
	public static Subject properties(String... tabs)
	{
		return new Subject(Kind.PROPERTIES, null, null, tabs.clone());
	}

	public static Subject calculateClosure()
	{
		return new Subject(Kind.CALCULATE_CLOSURE, null, null, new String[0]);
	}

	public static Subject lastClosure()
	{
		return new Subject(Kind.LAST_CLOSURE, null, null, new String[0]);
	}

	public Kind getKind()
	{
		return kind;
	}

	/** @return the segment of a SEGMENT subject */
	public Segment getSegment()
	{
		return segment;
	}

	/** @return a SEGMENT subject's tab of the segment dialog: Left, Right or null */
	public String getSegmentTab()
	{
		return segmentTab;
	}

	/** @return a PROPERTIES subject's tab titles, outermost first */
	public String[] getTabs()
	{
		return tabs.clone();
	}

	@Override
	public String toString()
	{
		switch (kind)
		{
		case SEGMENT:
			return "segment " + segment.getName() + (segmentTab == null ? "" : " " + segmentTab);
		case PROPERTIES:
			return "properties " + String.join(" > ", tabs);
		case CALCULATE_CLOSURE:
			return "calculate closure";
		case LAST_CLOSURE:
			return "last closure";
		default:
			return "none";
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Subject))
			return false;
		Subject subject = (Subject) other;
		return kind == subject.kind && segment == subject.segment
			&& (segmentTab == null ? subject.segmentTab == null : segmentTab.equals(subject.segmentTab))
			&& Arrays.equals(tabs, subject.tabs);
	}

	@Override
	public int hashCode()
	{
		return kind.hashCode() * 31 + Arrays.hashCode(tabs);
	}
}
