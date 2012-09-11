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

/** @author Daniel Bechler */
abstract class AbstractDiffer<T extends Node> implements Differ<T>, Configurable
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractDiffer.class);

	static final ThreadLocal<CircularReferenceDetector> WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL;
	static final ThreadLocal<CircularReferenceDetector> BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL;

	static
	{
		WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL = new CircularReferenceDetectorThreadLocal();
		BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL = new CircularReferenceDetectorThreadLocal();
	}

	private DelegatingObjectDiffer delegate;

	protected AbstractDiffer()
	{
	}

	protected AbstractDiffer(final DelegatingObjectDiffer delegate)
	{
		Assert.notNull(delegate, "delegate");
		this.delegate = delegate;
	}

	@Override
	public final T compare(final Node parentNode, final Instances instances)
	{
		final CircularReferenceDetector workingCircularReferenceDetector =
				WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get();
		final CircularReferenceDetector baseCircularReferenceDetector =
				BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.get();
		final boolean destroyOnReturn = workingCircularReferenceDetector.isNew();
		T node;
		try
		{
			workingCircularReferenceDetector.push(instances.getWorking());
			baseCircularReferenceDetector.push(instances.getBase());
			try
			{
				node = internalCompare(parentNode, instances);
			}
			finally
			{
				workingCircularReferenceDetector.remove(instances.getWorking());
				baseCircularReferenceDetector.remove(instances.getBase());
			}
		}
		catch (CircularReferenceDetector.CircularReferenceException e)
		{
			node = newNode(parentNode, instances);
			node.setState(Node.State.CIRCULAR);
			logger.warn("Detected circular reference in node at path {}. " +
					"Going deeper would cause an infinite loop, so I'll stop looking at " +
					"this instance along the current path.", node.getPropertyPath());
		}
		if (destroyOnReturn)
		{
			WORKING_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.remove();
			BASE_CIRCULAR_REFERENCE_DETECTOR_THREAD_LOCAL.remove();
		}
		return node;
	}

	protected abstract T internalCompare(Node parentNode, Instances instances);

	protected abstract T newNode(Node parentNode, Instances instances);

	public final DelegatingObjectDiffer getDelegate()
	{
		return delegate;
	}

	public final void setDelegate(final DelegatingObjectDiffer delegate)
	{
		Assert.notNull(delegate, "delegate");
		this.delegate = delegate;
	}

	public final Configuration getConfiguration()
	{
		return delegate.getConfiguration();
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
