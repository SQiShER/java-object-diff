/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.diff.sequence;

public final class Sequence
{
	private static final Sequence EMPTY_SEQUENCE = new Sequence(0, 0, 0);
	private final int workingOffset;
	private final int baseOffset;
	private final int length;

	public static Sequence emptySequence()
	{
		return EMPTY_SEQUENCE;
	}

	public Sequence(final int workingOffset, final int baseOffset, final int length)
	{
		this.workingOffset = workingOffset;
		this.baseOffset = baseOffset;
		this.length = length;
	}

	public int getWorkingOffset()
	{
		return workingOffset;
	}

	public boolean containsWorking(final int index)
	{
		return index > workingOffset && index <= workingOffset + length;
	}

	public boolean containsBase(final int index)
	{
		return index > baseOffset && index <= baseOffset + length;
	}

	public int getBaseOffset()
	{
		return baseOffset;
	}

	public int length()
	{
		return length;
	}

	@Override
	public String toString()
	{
		return "Sequence{" + "workingOffset=" + workingOffset + ", baseOffset=" + baseOffset + ", length=" + length + '}';
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final Sequence sequence = (Sequence) o;

		if (workingOffset != sequence.workingOffset)
		{
			return false;
		}
		if (baseOffset != sequence.baseOffset)
		{
			return false;
		}
		if (length != sequence.length)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = workingOffset;
		result = 31 * result + baseOffset;
		result = 31 * result + length;
		return result;
	}
}
