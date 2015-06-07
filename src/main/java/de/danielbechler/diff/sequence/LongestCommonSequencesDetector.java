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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LongestCommonSequencesDetector
{
	private static final Logger logger = LoggerFactory.getLogger(LongestCommonSequencesDetector.class);

	private LongestCommonSequencesDetector()
	{
	}

	public static List<Sequence> findSequences(final List working, final List base)
	{
		final int[][] matrix = determinePossibleCommonSequences(working, base);
		return extractLongestSequences(matrix);
	}

	private static int[][] determinePossibleCommonSequences(final List working, final List base)
	{
		final int[][] matrix = new int[working.size() + 1][base.size() + 1];
		for (int i = 0; i < working.size(); i++)
		{
			for (int j = 0; j < base.size(); j++)
			{
				matrix[i + 1][j + 1] = working.get(i).equals(base.get(j)) ? matrix[i][j] + 1 : 0;
			}
		}
		return matrix;
	}

	private static List<Sequence> extractLongestSequences(final int[][] matrix)
	{
		final Sequence longestAvailableSequence = findLongestAvailableSequence(matrix);
		if (longestAvailableSequence.length() > 0)
		{
			removeSequenceFromMatrix(matrix, longestAvailableSequence);
			final List<Sequence> sequences = new ArrayList<Sequence>();
			sequences.add(longestAvailableSequence);
			sequences.addAll(extractLongestSequences(matrix));
			return sequences;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	private static Sequence findLongestAvailableSequence(final int[][] matrix)
	{
		Sequence longestAvailableSequence = Sequence.emptySequence();
		for (int i = matrix.length - 1; i >= 0; i--)
		{
			for (int j = matrix[i].length - 1; j >= 0; j--)
			{
				final int length = matrix[i][j];
				if (length >= longestAvailableSequence.length())
				{
					longestAvailableSequence = new Sequence(i - length, j - length, length);
				}
			}
		}
		return longestAvailableSequence;
	}

	/**
	 * Removes all sequences contained in the given {@linkplain Sequence}. This includes longer sequences, that
	 * originate within the coordinates to the given sequence.
	 *
	 * @param matrix                   The matrix representing all known sequences.
	 * @param longestAvailableSequence The sequence to remove from the matrix.
	 */
	private static void removeSequenceFromMatrix(final int[][] matrix, final Sequence longestAvailableSequence)
	{
		logMatrix(matrix);
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				if (longestAvailableSequence.containsWorking(i) || longestAvailableSequence.containsBase(j))
				{
					removeSequenceStartingAt(matrix, i, j);
				}
			}
		}
		logMatrix(matrix);
	}

	private static void removeSequenceStartingAt(final int[][] matrix, final int workingOffset, final int baseOffset)
	{
		for (int i = workingOffset; i < matrix.length; i++)
		{
			for (int j = baseOffset; j < matrix[i].length; j++)
			{
				switch (matrix[i][j])
				{
					case 0:
						return;
					case 1:
						matrix[i][j]--;
						removeSequenceStartingAt(matrix, i + 1, j + 1);
						break;
					default:
						assert matrix[i][j] >= 0 : "BUG: it should not be possible to end up with negative matrix values";
						/* TODO Do we need to make the step size dynamic in order to properly adjust the end of this
								sequence in case it ends outside the boundaries of the originating sequence? Is that
								even possible? Or wouldn't that sequence then be the longest one? */
						matrix[i][j] = 0;
						removeSequenceStartingAt(matrix, i + 1, j + 1);
						break;
				}
			}
		}
	}

	private static void logMatrix(final int[][] matrix)
	{
		if (logger.isDebugEnabled())
		{
			final StringBuilder stringBuilder = new StringBuilder();
			for (final int[] working : matrix)
			{
				for (final int base : working)
				{
					stringBuilder.append(base).append(' ');
				}
				stringBuilder.append('\n');
			}
			logger.debug("{}x{} matrix:\n{}", matrix[0].length, matrix.length, stringBuilder.toString().trim());
		}
	}
}
