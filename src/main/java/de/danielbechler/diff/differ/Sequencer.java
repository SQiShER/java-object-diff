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

package de.danielbechler.diff.differ;

import java.util.ArrayList;
import java.util.List;

public class Sequencer
{
	private Sequencer()
	{
	}

	public static List<Sequence> findSequences(final List working, final List base)
	{
		final int[][] matrix = computeMatrix(working, base);
//		final List<Sequence> sequences = new ArrayList<Sequence>();
//		sequences.add(findLongestAvailableSequence(matrix));
		return extractSequences(matrix);
//		return sequences;
	}

	static int[][] computeMatrix(final List working, final List base)
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

	private static List<Sequence> extractSequences(final int[][] matrix)
	{
		final List<Sequence> sequences = new ArrayList<Sequence>();
		final Sequence longestAvailableSequence = findLongestAvailableSequence(matrix);
		if (longestAvailableSequence.length() > 0)
		{
			System.out.println("=============");
			printMatrix(matrix);
			removeSequenceFromMatrix(matrix, longestAvailableSequence);
			System.out.println("-------------");
			printMatrix(matrix);
			sequences.add(longestAvailableSequence);
			sequences.addAll(extractSequences(matrix));
		}
		return sequences;
	}

	private static void removeSequence(final int[][] matrix, final int workingOffset, final int baseOffset)
	{
		for (int i = workingOffset; i < matrix.length; i++)
		{
			for (int j = baseOffset; j < matrix[i].length; j++)
			{
				final int value = matrix[i][j];
				if (value == 0)
				{
					return;
				}
				else if (value == 1)
				{
					matrix[i][j]--;
					removeSequence(matrix, i + 1, j + 1);
				}
				else
				{
					matrix[i][j] = 0;
				}
			}
		}
	}

	public static void printMatrix(final int[][] matrix)
	{
		for (final int[] working : matrix)
		{
			for (final int base : working)
			{
				System.out.print(base + " ");
			}
			System.out.println();
		}
	}

	private static void removeSequenceFromMatrix(final int[][] matrix, final Sequence longestAvailableSequence)
	{
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				if (longestAvailableSequence.containsWorking(i) || longestAvailableSequence.containsBase(j))
				{
					removeSequence(matrix, i, j);
				}
			}
		}
	}

	private static Sequence findLongestAvailableSequence(final int[][] matrix)
	{
		Sequence longestAvailableSequence = new Sequence(0, 0, 0);
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
}
