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
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;
import org.slf4j.*;

import static de.danielbechler.diff.CircularReferenceDetector.*;

/** @author Daniel Bechler */
@SuppressWarnings("MethodMayBeStatic")
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
			return newDefaultNode(parentNode, instances, type);
		}
		return delegateWithCircularReferenceTracking(parentNode, instances);
	}

	private Node delegateWithCircularReferenceTracking(final Node parentNode, final Instances instances)
	{
		Node node;
		try
		{
			rememberInstances(parentNode, instances);
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
			node = newCircularNode(parentNode, instances, e.getPropertyPath());
			logCircularReference(node.getPropertyPath());
		}
		if (parentNode == null)
		{
			resetInstanceMemory();
		}
		return node;
	}

	private Node findNodeMatchingPropertyPath(final Node node, final PropertyPath propertyPath)
	{
		if (node == null)
		{
			return null;
		}
		if (node.matches(propertyPath))
		{
			return node;
		}
		return findNodeMatchingPropertyPath(node.getParentNode(), propertyPath);
	}

	private static Node newDefaultNode(final Node parentNode, final Instances instances, final Class<?> type)
	{
		return new DefaultNode(parentNode, instances.getSourceAccessor(), type);
	}

	private Node newCircularNode(final Node parentNode,
								 final Instances instances,
								 final PropertyPath circleStartPath)
	{
		final Node node = new DefaultNode(parentNode, instances.getSourceAccessor(), instances.getType());
		node.setState(Node.State.CIRCULAR);
		node.setCircleStartPath(circleStartPath);
		node.setCircleStartNode(findNodeMatchingPropertyPath(parentNode, circleStartPath));
		return node;
	}

	private static void logCircularReference(final PropertyPath propertyPath)
	{
		logger.warn("Detected circular reference in node at path {}. " +
				"Going deeper would cause an infinite loop, so I'll stop looking at " +
				"this instance along the current path.", propertyPath);
	}

	private Node compare(final Node parentNode, final Instances instances)
	{
		final Differ<?> differ = differFactory.createDiffer(instances.getType(), this);
		if (differ != null)
		{
			return differ.compare(parentNode, instances);
		}
		throw new IllegalStateException("Couldn't create Differ for type '" + instances.getType() +
				"'. This mustn't happen, as there should always be a fallback differ.");
	}

	protected void resetInstanceMemory()
	{
		WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.remove();
		BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.remove();
	}

	protected void forgetInstances(final Instances instances)
	{
		WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get().remove(instances.getWorking());
		BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get().remove(instances.getBase());
	}

	protected void rememberInstances(final Node parentNode, final Instances instances)
	{
		final PropertyPath propertyPath = instances.getPropertyPath(parentNode);
		WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get().push(instances.getWorking(), propertyPath);
		BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get().push(instances.getBase(), propertyPath);
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
