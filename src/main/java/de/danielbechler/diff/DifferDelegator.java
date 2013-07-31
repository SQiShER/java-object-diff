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

import static de.danielbechler.diff.CircularReferenceDetector.*;

/** @author Daniel Bechler */
@SuppressWarnings("MethodMayBeStatic")
class DifferDelegator
{
	private final DifferFactory differFactory;
	private final CircularReferenceDetectorFactory circularReferenceDetectorFactory;
	private CircularReferenceDetector workingCircularReferenceDetector;
	private CircularReferenceDetector baseCircularReferenceDetector;

	public DifferDelegator(final DifferFactory differFactory,
						   final CircularReferenceDetectorFactory circularReferenceDetectorFactory)
	{
		Assert.notNull(differFactory, "differFactory");
		Assert.notNull(circularReferenceDetectorFactory, "circularReferenceDetectorFactory");
		this.differFactory = differFactory;
		this.circularReferenceDetectorFactory = circularReferenceDetectorFactory;
		resetInstanceMemory();
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
			differFactory.getConfiguration().getExceptionListener().onCircularReferenceException(node);
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

	protected final void resetInstanceMemory()
	{
		workingCircularReferenceDetector = circularReferenceDetectorFactory.create();
		baseCircularReferenceDetector = circularReferenceDetectorFactory.create();
	}

	protected void forgetInstances(final Instances instances)
	{
		workingCircularReferenceDetector.remove(instances.getWorking());
		baseCircularReferenceDetector.remove(instances.getBase());
	}

	protected void rememberInstances(final Node parentNode, final Instances instances)
	{
		final PropertyPath propertyPath = instances.getPropertyPath(parentNode);
		workingCircularReferenceDetector.push(instances.getWorking(), propertyPath);
		baseCircularReferenceDetector.push(instances.getBase(), propertyPath);
	}
}
