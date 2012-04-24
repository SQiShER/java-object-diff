/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.danielbechler.diff.mock;

import de.danielbechler.diff.annotation.*;

/** @author Daniel Bechler */
@ObjectDiffEqualsOnlyType
public class ObjectWithPropertyAnnotations extends ObjectWithHashCodeAndEquals
{
	private String ignored;
	private String equalsOnly;
	private String categorized;

	public ObjectWithPropertyAnnotations(final String key)
	{
		super(key);
	}

	public ObjectWithPropertyAnnotations(final String key, final String value)
	{
		super(key, value);
	}

	@ObjectDiffProperty(ignore = true)
	public String getIgnored()
	{
		return ignored;
	}

	public void setIgnored(final String ignored)
	{
		this.ignored = ignored;
	}

	@ObjectDiffProperty(equalsOnly = true)
	public String getEqualsOnly()
	{
		return equalsOnly;
	}

	public void setEqualsOnly(final String equalsOnly)
	{
		this.equalsOnly = equalsOnly;
	}

	@ObjectDiffProperty(categories = {"foo"})
	public String getCategorized()
	{
		return categorized;
	}

	public void setCategorized(final String categorized)
	{
		this.categorized = categorized;
	}
}
