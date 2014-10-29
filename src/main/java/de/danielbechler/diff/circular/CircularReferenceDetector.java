/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.circular;

import de.danielbechler.diff.path.NodePath;
import de.danielbechler.util.Assert;

import java.util.LinkedList;

import static de.danielbechler.util.Objects.isEqual;

/**
 * @author Daniel Bechler
 */
public class CircularReferenceDetector
{
	/**
	 * It would be better to use <code>java.util.Deque</code> instead of the linked list, but that was first
	 * introduced in Java 6 and would break compatibility with Java 5.
	 */
	@SuppressWarnings("TypeMayBeWeakened")
	private final LinkedList<Entry> stack = new LinkedList<Entry>();

	private ReferenceMatchingMode referenceMatchingMode = ReferenceMatchingMode.EQUALITY_OPERATOR;

	public CircularReferenceDetector(final ReferenceMatchingMode referenceMatchingMode)
	{
		Assert.notNull(referenceMatchingMode, "referenceMatchingMode");
		this.referenceMatchingMode = referenceMatchingMode;
	}

	public void push(final Object instance, final NodePath nodePath)
	{
		if (instance == null)
		{
			return;
		}
		if (knows(instance))
		{
			throw new CircularReferenceException(entryForInstance(instance).getNodePath());
		}
		final Entry entry = new Entry(nodePath, instance);
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

	protected boolean isMatch(final Object anObject, final Object anotherObject)
	{
		if (referenceMatchingMode == ReferenceMatchingMode.EQUALITY_OPERATOR)
		{
			return anotherObject == anObject;
		}
		else if (referenceMatchingMode == ReferenceMatchingMode.EQUALS_METHOD)
		{
			return (anotherObject == anObject) || isEqual(anObject, anotherObject);
		}
		else
		{
			throw new IllegalStateException("Missing reference matching mode");
		}
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

	public static enum ReferenceMatchingMode
	{
		/**
		 * Compares objects using the <code>==</code> operator.
		 */
		EQUALITY_OPERATOR,

		/**
		 * Compares objects using {@linkplain Object#equals(Object)}.
		 */
		EQUALS_METHOD
	}

	private static class Entry
	{
		private final NodePath nodePath;
		private final Object instance;

		private Entry(final NodePath nodePath, final Object instance)
		{
			this.nodePath = nodePath;
			this.instance = instance;
		}

		public NodePath getNodePath()
		{
			return nodePath;
		}

		public Object getInstance()
		{
			return instance;
		}

		@Override
		public String toString()
		{
			return nodePath.toString() + "{" + instance.toString() + "}";
		}
	}

	public static class CircularReferenceException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("NonSerializableFieldInSerializableClass")
		private final NodePath nodePath;

		public CircularReferenceException(final NodePath nodePath)
		{
			this.nodePath = nodePath;
		}

		public NodePath getNodePath()
		{
			return nodePath;
		}

		@Override
		public Throwable fillInStackTrace()
		{
			return this;
		}
	}
}
