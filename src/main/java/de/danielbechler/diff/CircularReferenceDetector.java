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

import java.util.*;

/** @author Daniel Bechler */
final class CircularReferenceDetector
{
	private final Deque<Object> stack = new LinkedList<Object>();

	private boolean isNew = true;

	public CircularReferenceDetector()
	{
	}

	public boolean isNew()
	{
		return isNew;
	}

	public void push(final Object instance)
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
			throw new CircularReferenceException();
		}
		stack.addLast(instance);
	}

	public boolean knows(final Object needle)
	{
		for (final Object object : stack)
		{
			if (object == needle)
			{
				return true;
			}
		}
		return false;
	}

	public void remove(final Object instance)
	{
		if (instance == null)
		{
			return;
		}
		if (stack.getLast() == instance)
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

		public CircularReferenceException()
		{
		}

		@Override
		public Throwable fillInStackTrace()
		{
			return null;
		}
	}
}
