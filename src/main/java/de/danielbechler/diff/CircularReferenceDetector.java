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

import java.util.*;

/** @author Daniel Bechler */
class CircularReferenceDetector
{
	private final Deque<Entry> stack = new LinkedList<Entry>();

	private boolean isNew = true;
	private boolean treatEqualObjectsAsIdentical;

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

	/** @deprecated Only used in tests. */
	@Deprecated
	public boolean isNew()
	{
		return isNew;
	}

	public void setTreatEqualObjectsAsIdentical(final boolean treatEqualObjectsAsIdentical)
	{
		this.treatEqualObjectsAsIdentical = treatEqualObjectsAsIdentical;
	}

	public void push(final Object instance, final PropertyPath propertyPath)
	{
		if (instance == null)
		{
			return;
		}
		if (isNew)
		{
			isNew = false;
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
		if (treatEqualObjectsAsIdentical)
		{
			return anotherObject != null && anObject != null && anotherObject.equals(anObject);
		}
		else
		{
			return anotherObject == anObject;
		}
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
}
