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

import de.danielbechler.diff.node.*;

import static de.danielbechler.diff.Configuration.CircularReferenceMatchingMode.*;

/**
 * This is the entry point for all comparisons. It determines the type of the given objects and passes them to
 * the appropriate {@link Differ}.
 *
 * @author Daniel Bechler
 */
public class ObjectDiffer
{
	private static final CircularReferenceDetectorFactory CIRCULAR_REFERENCE_DETECTOR_WITH_EQUALITY_OPERATOR_FACTORY = new CircularReferenceDetectorFactory()
	{
		public CircularReferenceDetector create()
		{
			final CircularReferenceDetector circularReferenceDetector = new CircularReferenceDetector();
			circularReferenceDetector.setReferenceMatchingMode(CircularReferenceDetector.ReferenceMatchingMode.EQUALITY_OPERATOR);
			return circularReferenceDetector;
		}
	};
	private static final CircularReferenceDetectorFactory CIRCULAR_REFERENCE_DETECTOR_WITH_EQUALS_METHOD_FACTORY = new CircularReferenceDetectorFactory()
	{
		public CircularReferenceDetector create()
		{
			final CircularReferenceDetector circularReferenceDetector = new CircularReferenceDetector();
			circularReferenceDetector.setReferenceMatchingMode(CircularReferenceDetector.ReferenceMatchingMode.EQUALS_METHOD);
			return circularReferenceDetector;
		}
	};
	private final Configuration configuration;
	private final DifferDelegator delegator;

	ObjectDiffer(final Configuration configuration)
	{
		this.configuration = configuration;
		this.delegator = new DifferDelegator(new DifferFactory(configuration), newCircularReferenceDetectorFactory(configuration));
	}

	/**
	 * Recursively inspects the given objects and returns a node representing their differences. Both objects
	 * have be have the same type.
	 *
	 * @param working This object will be treated as the successor of the <code>base</code> object.
	 * @param base    This object will be treated as the predecessor of the <code>working</code> object.
	 * @param <T>     The type of the objects to compare.
	 *
	 * @return A node representing the differences between the given objects.
	 */
	public <T> Node compare(final T working, final T base)
	{
		return delegator.delegate(Node.ROOT, Instances.of(working, base));
	}

	/**
	 * @deprecated The configuration will become an immutable object created by the configuration builder. The
	 *             only way to configure an ObjectDiffer is by creating a new instance via {@link
	 *             ObjectDifferFactory}. Therefore there will be no need for this getter anymore.
	 */
	@Deprecated
	public Configuration getConfiguration()
	{
		return configuration;
	}

	private static CircularReferenceDetectorFactory newCircularReferenceDetectorFactory(final Configuration configuration)
	{
		if (configuration.getCircularReferenceMatchingMode() == EQUALS_METHOD)
		{
			return CIRCULAR_REFERENCE_DETECTOR_WITH_EQUALS_METHOD_FACTORY;
		}
		else if (configuration.getCircularReferenceMatchingMode() == EQUALITY_OPERATOR)
		{
			return CIRCULAR_REFERENCE_DETECTOR_WITH_EQUALITY_OPERATOR_FACTORY;
		}
		throw new IllegalStateException();
	}
}
