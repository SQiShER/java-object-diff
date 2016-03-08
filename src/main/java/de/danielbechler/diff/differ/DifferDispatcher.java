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

package de.danielbechler.diff.differ;

import de.danielbechler.diff.access.Accessor;
import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.category.CategoryResolver;
import de.danielbechler.diff.introspection.PropertyReadException;
import de.danielbechler.diff.circular.CircularReferenceDetector;
import de.danielbechler.diff.circular.CircularReferenceDetectorFactory;
import de.danielbechler.diff.circular.CircularReferenceExceptionHandler;
import de.danielbechler.diff.filtering.IsReturnableResolver;
import de.danielbechler.diff.inclusion.IsIgnoredResolver;
import de.danielbechler.diff.introspection.PropertyAccessExceptionHandler;
import de.danielbechler.diff.introspection.PropertyAccessExceptionHandlerResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.danielbechler.diff.circular.CircularReferenceDetector.CircularReferenceException;

/**
 * @author Daniel Bechler
 */
public class DifferDispatcher
{
	private static final Logger logger = LoggerFactory.getLogger(DifferDispatcher.class);
	private final DifferProvider differProvider;
	private final CircularReferenceDetectorFactory circularReferenceDetectorFactory;
	private final CircularReferenceExceptionHandler circularReferenceExceptionHandler;
	private final IsIgnoredResolver isIgnoredResolver;
	private final CategoryResolver categoryResolver;
	private final IsReturnableResolver isReturnableResolver;
	private final PropertyAccessExceptionHandlerResolver propertyAccessExceptionHandlerResolver;
	private static final ThreadLocal<CircularReferenceDetector> workingThreadLocal = new ThreadLocal<CircularReferenceDetector>();
	private static final ThreadLocal<CircularReferenceDetector> baseThreadLocal = new ThreadLocal<CircularReferenceDetector>();

	public DifferDispatcher(final DifferProvider differProvider,
							final CircularReferenceDetectorFactory circularReferenceDetectorFactory,
							final CircularReferenceExceptionHandler circularReferenceExceptionHandler,
							final IsIgnoredResolver ignoredResolver,
							final IsReturnableResolver returnableResolver,
							final PropertyAccessExceptionHandlerResolver propertyAccessExceptionHandlerResolver,
							final CategoryResolver categoryResolver)
	{
		Assert.notNull(differProvider, "differFactory");
		this.differProvider = differProvider;

		Assert.notNull(ignoredResolver, "ignoredResolver");
		this.isIgnoredResolver = ignoredResolver;

		Assert.notNull(categoryResolver, "categoryResolver");
		this.categoryResolver = categoryResolver;

		this.circularReferenceDetectorFactory = circularReferenceDetectorFactory;
		this.circularReferenceExceptionHandler = circularReferenceExceptionHandler;
		this.isReturnableResolver = returnableResolver;
		this.propertyAccessExceptionHandlerResolver = propertyAccessExceptionHandlerResolver;

		resetInstanceMemory();
	}

	public final void resetInstanceMemory()
	{
		workingThreadLocal.set(circularReferenceDetectorFactory.createCircularReferenceDetector());
		baseThreadLocal.set(circularReferenceDetectorFactory.createCircularReferenceDetector());
	}

	public final void clearInstanceMemory()
	{
		workingThreadLocal.remove();
		baseThreadLocal.remove();
	}

	/**
	 * Delegates the call to an appropriate {@link Differ}.
	 *
	 * @return A node representing the difference between the given {@link Instances}.
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
		if (node != null)
		{
			node.addCategories(categoryResolver.resolveCategories(node));
		}
		return node;
	}

	private DiffNode compare(final DiffNode parentNode, final Instances parentInstances, final Accessor accessor)
	{
		final DiffNode node = new DiffNode(parentNode, accessor, null);
		if (isIgnoredResolver.isIgnored(node))
		{
			node.setState(DiffNode.State.IGNORED);
			return node;
		}

		final Instances accessedInstances;
		if (accessor instanceof PropertyAwareAccessor)
		{
			final PropertyAwareAccessor propertyAwareAccessor = (PropertyAwareAccessor) accessor;
			try
			{
				accessedInstances = parentInstances.access(accessor);
			}
			catch (final PropertyReadException e)
			{
				node.setState(DiffNode.State.INACCESSIBLE);
				final Class<?> parentType = parentInstances.getType();
				final String propertyName = propertyAwareAccessor.getPropertyName();
				final PropertyAccessExceptionHandler exceptionHandler = propertyAccessExceptionHandlerResolver
						.resolvePropertyAccessExceptionHandler(parentType, propertyName);
				if (exceptionHandler != null)
				{
					exceptionHandler.onPropertyReadException(e, node);
				}
				return node;
			}
		}
		else
		{
			accessedInstances = parentInstances.access(accessor);
		}

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
		DiffNode node = null;
		try
		{
			rememberInstances(parentNode, instances);
			try
			{
				node = compare(parentNode, instances);
			}
			finally
			{
				if (node != null)
				{
					forgetInstances(parentNode, instances);
				}
			}
		}
		catch (final CircularReferenceException e)
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

	protected static void forgetInstances(final DiffNode parentNode, final Instances instances)
	{
		final NodePath nodePath = getNodePath(parentNode, instances);
		logger.debug("[ {} ] Forgetting --- WORKING: {} <=> BASE: {}", nodePath, instances.getWorking(), instances.getBase());
		workingThreadLocal.get().remove(instances.getWorking());
		baseThreadLocal.get().remove(instances.getBase());
	}

	private static NodePath getNodePath(final DiffNode parentNode, final Instances instances)
	{
		if (parentNode == null)
		{
			return NodePath.withRoot();
		}
		else
		{
			final NodePath parentPath = parentNode.getPath();
			final ElementSelector elementSelector = instances.getSourceAccessor().getElementSelector();
			return NodePath.startBuildingFrom(parentPath).element(elementSelector).build();
		}
	}

	protected static void rememberInstances(final DiffNode parentNode, final Instances instances)
	{
		final NodePath nodePath = getNodePath(parentNode, instances);
		logger.debug("[ {} ] Remembering --- WORKING: {} <=> BASE: {}", nodePath, instances.getWorking(), instances.getBase());
		transactionalPushToCircularReferenceDetectors(nodePath, instances);
	}

	private static void transactionalPushToCircularReferenceDetectors(final NodePath nodePath, final Instances instances)
	{
		workingThreadLocal.get().push(instances.getWorking(), nodePath);

		// TODO This needs to be solved more elegantly. If the push for one of these detectors fails,
		// we need to make sure to revert the push to the other one, if it already happened.
		try
		{
			baseThreadLocal.get().push(instances.getBase(), nodePath);
		}
		catch (final CircularReferenceException e)
		{
			workingThreadLocal.get().remove(instances.getWorking()); // rollback
			throw e;
		}
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
}
