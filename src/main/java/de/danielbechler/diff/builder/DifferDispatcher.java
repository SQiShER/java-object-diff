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

package de.danielbechler.diff.builder;

import de.danielbechler.diff.Accessor;
import de.danielbechler.diff.Differ;
import de.danielbechler.diff.DifferProvider;
import de.danielbechler.diff.Instances;
import de.danielbechler.diff.IsIgnoredResolver;
import de.danielbechler.diff.IsReturnableResolver;
import de.danielbechler.diff.circular.CircularReferenceDetector;
import de.danielbechler.diff.circular.CircularReferenceDetectorFactory;
import de.danielbechler.diff.circular.CircularReferenceExceptionHandler;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.nodepath.ElementSelector;
import de.danielbechler.diff.nodepath.NodePath;
import de.danielbechler.util.Assert;

import static de.danielbechler.diff.circular.CircularReferenceDetector.CircularReferenceException;

/**
 * @author Daniel Bechler
 */
public class DifferDispatcher
{
	private final DifferProvider differProvider;
	private final CircularReferenceDetectorFactory circularReferenceDetectorFactory;
	private final CircularReferenceExceptionHandler circularReferenceExceptionHandler;
	private final IsIgnoredResolver isIgnoredResolver;
	private final IsReturnableResolver isReturnableResolver;
	private CircularReferenceDetector workingCircularReferenceDetector;
	private CircularReferenceDetector baseCircularReferenceDetector;

	public DifferDispatcher(final DifferProvider differProvider,
							final CircularReferenceDetectorFactory circularReferenceDetectorFactory,
							final CircularReferenceExceptionHandler circularReferenceExceptionHandler,
							final IsIgnoredResolver ignoredResolver,
							final IsReturnableResolver returnableResolver)
	{
		Assert.notNull(differProvider, "differFactory");
		this.differProvider = differProvider;

		Assert.notNull(ignoredResolver, "ignoredResolver");
		this.isIgnoredResolver = ignoredResolver;

		this.circularReferenceDetectorFactory = circularReferenceDetectorFactory;
		this.circularReferenceExceptionHandler = circularReferenceExceptionHandler;
		this.isReturnableResolver = returnableResolver;

		resetInstanceMemory();
	}

	private static DiffNode findNodeMatchingPropertyPath(final DiffNode node, final NodePath nodePath)
	{
		if (node == null)
		{
			return null;
		}
		if (node.matches(nodePath))
		{
			return node;
		}
		return findNodeMatchingPropertyPath(node.getParentNode(), nodePath);
	}

	private static DiffNode newCircularNode(final DiffNode parentNode,
											final Instances instances,
											final NodePath circleStartPath)
	{
		final DiffNode node = new DiffNode(parentNode, instances.getSourceAccessor(), instances.getType());
		node.setState(DiffNode.State.CIRCULAR);
		node.setCircleStartPath(circleStartPath);
		node.setCircleStartNode(findNodeMatchingPropertyPath(parentNode, circleStartPath));
		return node;
	}

	/**
	 * Delegates the call to an appropriate {@link de.danielbechler.diff.Differ}.
	 *
	 * @return A node representing the difference between the given {@link de.danielbechler.diff.Instances}.
	 */
	public DiffNode dispatch(final DiffNode parentNode,
							 final Instances parentInstances,
							 final Accessor accessor)
	{
		Assert.notNull(parentInstances, "parentInstances");
		Assert.notNull(accessor, "accessor");

		final DiffNode node = compare(parentNode, parentInstances, accessor);
		if (parentNode != null && isReturnableResolver.isReturnable(node))
		{
			parentNode.addChild(node);
		}
		return node;
	}

	private DiffNode compare(final DiffNode parentNode,
							 final Instances parentInstances,
							 final Accessor accessor)
	{
		final DiffNode node = new DiffNode(parentNode, accessor, null);
		if (isIgnoredResolver.isIgnored(node))
		{
			node.setState(DiffNode.State.IGNORED);
			return node;
		}

		final Instances accessedInstances = parentInstances.access(accessor);
		if (accessedInstances.areNull())
		{
			return new DiffNode(parentNode, accessedInstances.getSourceAccessor(), accessedInstances.getType());
		}
		else
		{
			return compareWithCircularReferenceTracking(parentNode, accessedInstances);
		}
	}

	private DiffNode compareWithCircularReferenceTracking(final DiffNode parentNode,
														  final Instances instances)
	{
		DiffNode node;
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
			node = newCircularNode(parentNode, instances, e.getNodePath());
			circularReferenceExceptionHandler.onCircularReferenceException(node);
		}
		if (parentNode == null)
		{
			resetInstanceMemory();
		}
		return node;
	}

	private DiffNode compare(final DiffNode parentNode, final Instances instances)
	{
		final Differ differ = differProvider.retrieveDifferForType(instances.getType());
		if (differ == null)
		{
			throw new IllegalStateException("Couldn't create Differ for type '" + instances.getType() +
					"'. This mustn't happen, as there should always be a fallback differ.");
		}
		return differ.compare(parentNode, instances);
	}

	protected final void resetInstanceMemory()
	{
		workingCircularReferenceDetector = circularReferenceDetectorFactory.createCircularReferenceDetector();
		baseCircularReferenceDetector = circularReferenceDetectorFactory.createCircularReferenceDetector();
	}

	protected void forgetInstances(final Instances instances)
	{
		workingCircularReferenceDetector.remove(instances.getWorking());
		baseCircularReferenceDetector.remove(instances.getBase());
	}

	protected void rememberInstances(final DiffNode parentNode, final Instances instances)
	{
		final NodePath nodePath;
		if (parentNode != null)
		{
			final NodePath parentPath = parentNode.getPath();
			final ElementSelector elementSelector = instances.getSourceAccessor().getElementSelector();
			nodePath = NodePath.startBuildingFrom(parentPath).element(elementSelector).build();
		}
		else
		{
			nodePath = NodePath.withRoot();
		}
		workingCircularReferenceDetector.push(instances.getWorking(), nodePath);
		baseCircularReferenceDetector.push(instances.getBase(), nodePath);
	}
}
