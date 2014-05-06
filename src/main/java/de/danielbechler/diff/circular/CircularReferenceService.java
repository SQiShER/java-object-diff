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

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Bechler
 */
public class CircularReferenceService implements CircularReferenceConfigurer, CircularReferenceDetectorFactory, CircularReferenceExceptionHandler
{
	private static final Logger logger = LoggerFactory.getLogger(CircularReferenceService.class);
	private final ObjectDifferBuilder objectDifferBuilder;

	private CircularReferenceMatchingMode circularReferenceMatchingMode = CircularReferenceMatchingMode.EQUALITY_OPERATOR;
	private CircularReferenceExceptionHandler circularReferenceExceptionHandler = new CircularReferenceExceptionHandler()
	{
		public void onCircularReferenceException(final DiffNode node)
		{
			final String message = "Detected circular reference in node at path {}. "
					+ "Going deeper would cause an infinite loop, so I'll stop looking at "
					+ "this instance along the current path.";
			logger.warn(message, node.getPath());
		}
	};

	public CircularReferenceService(final ObjectDifferBuilder objectDifferBuilder)
	{
		this.objectDifferBuilder = objectDifferBuilder;
	}

	public CircularReferenceConfigurer matchCircularReferencesUsing(final CircularReferenceMatchingMode matchingMode)
	{
		this.circularReferenceMatchingMode = matchingMode;
		return this;
	}

	public CircularReferenceConfigurer handleCircularReferenceExceptionsUsing(final CircularReferenceExceptionHandler exceptionHandler)
	{
		this.circularReferenceExceptionHandler = exceptionHandler;
		return this;
	}

	public ObjectDifferBuilder and()
	{
		return objectDifferBuilder;
	}

	public CircularReferenceDetector createCircularReferenceDetector()
	{
		if (circularReferenceMatchingMode == CircularReferenceMatchingMode.EQUALS_METHOD)
		{
			return new CircularReferenceDetector(CircularReferenceDetector.ReferenceMatchingMode.EQUALS_METHOD);
		}
		else if (circularReferenceMatchingMode == CircularReferenceMatchingMode.EQUALITY_OPERATOR)
		{
			return new CircularReferenceDetector(CircularReferenceDetector.ReferenceMatchingMode.EQUALITY_OPERATOR);
		}
		throw new IllegalStateException();
	}

	public void onCircularReferenceException(final DiffNode node)
	{
		if (circularReferenceExceptionHandler != null)
		{
			circularReferenceExceptionHandler.onCircularReferenceException(node);
		}
	}
}
