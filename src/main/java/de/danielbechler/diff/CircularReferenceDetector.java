/*
 * Copyright 2012 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff;

import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
class CircularReferenceDetector
{
	private final Deque<Entry> stack = new LinkedList<Entry>();

	private ReferenceMatchingMode referenceMatchingMode = ReferenceMatchingMode.EQUALITY_OPERATOR;

	private static class Entry
	{
		private final PropertyPath propertyPath;
		private final Object instance;

		private Entry(final PropertyPath propertyPath, final Object instance)
		{
			this.propertyPath = propertyPath;
			this.instance = instance;
		}

		public PropertyPath getPropertyPath()
		{
			return propertyPath;
		}

		public Object getInstance()
		{
			return instance;
		}
	}

	public CircularReferenceDetector()
	{
	}

	public void push(final Object instance, final PropertyPath propertyPath)
	{
		if (instance == null)
		{
			return;
		}
		if (knows(instance))
		{
			throw new CircularReferenceException(entryForInstance(instance).getPropertyPath());
		}
		final Entry entry = new Entry(propertyPath, instance);
		stack.addLast(entry);
	}

	public boolean knows(final Object needle)
	{
		for (final Entry entry : stack)
		{
			final Object instance = entry.getInstance();
			if (isMatch(needle, instance))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isMatch(final Object anObject, final Object anotherObject)
	{
		if (referenceMatchingMode == ReferenceMatchingMode.EQUALS_METHOD)
		{
			return anotherObject != null && anObject != null && anotherObject.equals(anObject);
		}
		else if (referenceMatchingMode == ReferenceMatchingMode.EQUALITY_OPERATOR)
		{
			return anotherObject == anObject;
		}
		throw new IllegalStateException("Missing reference matching mode");
	}

	private Entry entryForInstance(final Object instance)
	{
		for (final Entry entry : stack)
		{
			if (isMatch(instance, entry.getInstance()))
			{
				return entry;
			}
		}
		return null;
	}

	public void remove(final Object instance)
	{
		if (instance == null)
		{
			return;
		}
		if (isMatch(instance, stack.getLast().getInstance()))
		{
			stack.removeLast();
		}
		else
		{
			throw new IllegalArgumentException("Detected inconsistency in enter/leave sequence. Must always be LIFO.");
		}
	}

	public int size()
	{
		return stack.size();
	}

	public void setReferenceMatchingMode(final ReferenceMatchingMode referenceMatchingMode)
	{
		Assert.notNull(referenceMatchingMode, "referenceMatchingMode");
		this.referenceMatchingMode = referenceMatchingMode;
	}

	public static class CircularReferenceException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("NonSerializableFieldInSerializableClass")
		private final PropertyPath propertyPath;

		public CircularReferenceException(final PropertyPath propertyPath)
		{
			this.propertyPath = propertyPath;
		}

		public PropertyPath getPropertyPath()
		{
			return propertyPath;
		}

		@Override
		public Throwable fillInStackTrace()
		{
			return null;
		}
	}

	public static enum ReferenceMatchingMode
	{
		/** Compares objects using the <code>==</code> operator. */
		EQUALITY_OPERATOR,

		/** Compares objects using {@linkplain Object#equals(Object)}. */
		EQUALS_METHOD
	}
}
