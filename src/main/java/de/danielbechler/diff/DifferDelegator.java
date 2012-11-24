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
import de.danielbechler.util.*;
import org.slf4j.*;

import static de.danielbechler.diff.CircularReferenceDetector.*;

/** @author Daniel Bechler */
class DifferDelegator
{
	private static final Logger logger = LoggerFactory.getLogger(DifferDelegator.class);
	private static final ThreadLocal<CircularReferenceDetector> WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL = new CircularReferenceDetectorThreadLocal();
	private static final ThreadLocal<CircularReferenceDetector> BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL = new CircularReferenceDetectorThreadLocal();

	private final DifferFactory differFactory;

	public DifferDelegator(final DifferFactory differFactory)
	{
		this.differFactory = differFactory;
	}

	/**
	 * Delegates the call to an appropriate {@link de.danielbechler.diff.Differ}.
	 *
	 * @return A node representing the difference between the given {@link de.danielbechler.diff.Instances}.
	 */
	public Node delegate(final Node parentNode, final Instances instances)
	{
		Assert.notNull(instances, "instances");
		final Class<?> type = instances.getType();
		if (type == null)
		{
			return newSimpleNode(parentNode, instances, type);
		}
		return delegateWithCircularReferenceTracking(parentNode, instances);
	}

	private Node delegateWithCircularReferenceTracking(final Node parentNode, final Instances instances)
	{
		Node node;
		try
		{
			rememberInstances(instances);
			try
			{
				node = compare(parentNode, instances);
			}
			finally
			{
				forgetInstances(instances);
			}
		}
		catch (CircularReferenceException e)
		{
			node = newCircularNode(parentNode, instances);
			logCircularReference(node);
		}
		if (parentNode == null)
		{
			resetInstanceMemory();
		}
		return node;
	}

	private static Node newSimpleNode(final Node parentNode, final Instances instances, final Class<?> type)
	{
		return new DefaultNode(parentNode, instances.getSourceAccessor(), type);
	}

	private static Node newCircularNode(final Node parentNode, final Instances instances)
	{
		final Node node;
		node = new DefaultNode(parentNode, instances.getSourceAccessor(), instances.getType());
		node.setState(Node.State.CIRCULAR);
		return node;
	}

	private static void logCircularReference(final Node node)
	{
		logger.warn("Detected circular reference in node at path {}. " +
				"Going deeper would cause an infinite loop, so I'll stop looking at " +
				"this instance along the current path.", node.getPropertyPath());
	}

	private Node compare(final Node parentNode, final Instances instances)
	{
		return differFactory.createDiffer(instances.getType(), this).compare(parentNode, instances);
	}

	private static void resetInstanceMemory()
	{
		WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.remove();
		BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.remove();
	}

	private static void forgetInstances(final Instances instances)
	{
		WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get().remove(instances.getWorking());
		BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get().remove(instances.getBase());
	}

	private static void rememberInstances(final Instances instances)
	{
		WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get().push(instances.getWorking());
		BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get().push(instances.getBase());
	}

	private static final class CircularReferenceDetectorThreadLocal extends ThreadLocal<CircularReferenceDetector>
	{
		@Override
		protected CircularReferenceDetector initialValue()
		{
			return new CircularReferenceDetector();
		}
	}
}
