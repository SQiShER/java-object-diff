/*
 * Copyright 2013 Daniel Bechler
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

import de.danielbechler.diff.node.Accessor;
import de.danielbechler.diff.config.inclusion.IsIgnoredResolver;
import de.danielbechler.diff.config.filtering.IsReturnableResolver;
import de.danielbechler.diff.node.RootAccessor;
import de.danielbechler.diff.config.circular.CircularReferenceDetector;
import de.danielbechler.diff.config.circular.CircularReferenceDetectorFactory;
import de.danielbechler.diff.config.circular.CircularReferenceExceptionHandler;
import de.danielbechler.diff.mock.ObjectWithCircularReference;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.path.NodePath;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static de.danielbechler.diff.config.circular.CircularReferenceDetector.CircularReferenceException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Daniel Bechler
 */
public class DifferDispatcherShould
{
	private DifferDispatcher differDispatcher;

	@Mock
	private DifferProvider differProvider;
	@Mock
	private Accessor accessor;
	@Mock
	private CircularReferenceDetector circularReferenceDetector;
	@Mock
	private CircularReferenceDetectorFactory circularReferenceDetectorFactory;
	@Mock
	private CircularReferenceExceptionHandler circularReferenceExceptionHandler;
	@Mock
	private IsIgnoredResolver ignoredResolver;
	@Mock
	private IsReturnableResolver returnableResolver;
	@Mock
	private Instances instances;
	@Mock
	private Instances accessedInstances;

	@BeforeMethod
	public void setUp() throws Exception
	{
		initMocks(this);

		when(circularReferenceDetectorFactory.createCircularReferenceDetector()).thenReturn(circularReferenceDetector);
		when(instances.access(any(Accessor.class))).thenReturn(accessedInstances);
		when(accessedInstances.getSourceAccessor()).thenReturn(accessor);

		differDispatcher = new DifferDispatcher(differProvider, circularReferenceDetectorFactory, circularReferenceExceptionHandler, ignoredResolver, returnableResolver);
	}

	@SuppressWarnings("unchecked")
	private void given_the_delegated_node_is_circular(final NodePath circularStartPath)
	{
		doThrow(new CircularReferenceException(circularStartPath)).when(circularReferenceDetector)
				.push(any(), any(NodePath.class));

		when(instances.getSourceAccessor()).thenReturn(RootAccessor.getInstance());
		when(instances.getType()).then(returnType(Object.class));
		when(instances.getWorking()).thenReturn("");
		when(instances.getBase()).thenReturn("");
		when(differProvider.retrieveDifferForType(ObjectWithCircularReference.class)).thenReturn(mock(Differ.class));
	}

	@Test
	public void assign_the_circular_start_path_if_the_delegated_node_is_circular() throws Exception
	{
		final NodePath circularStartPath = NodePath.withRoot();
		given_the_delegated_node_is_circular(circularStartPath);

		final DiffNode node = differDispatcher.dispatch(DiffNode.ROOT, instances, accessor);

		assertThat(node.getCircleStartPath()).isEqualTo(circularStartPath);
	}

	@Test
	public void mark_node_as_circular_if_the_delegated_node_is_circular() throws Exception
	{
		given_the_delegated_node_is_circular(NodePath.withRoot());

		final DiffNode node = differDispatcher.dispatch(DiffNode.ROOT, instances, accessor);

		assertThat(node.getState()).isEqualTo(DiffNode.State.CIRCULAR);
	}

	@Test
	public void pass_node_to_onCircularReferenceException_method_of_the_exceptionListener_if_the_delegated_node_is_circular() throws Exception
	{
		given_the_delegated_node_is_circular(NodePath.withRoot());

		final DiffNode node = differDispatcher.dispatch(DiffNode.ROOT, instances, accessor);

		verify(circularReferenceExceptionHandler).onCircularReferenceException(node);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void throw_exception_if_no_differ_can_be_found_for_instance_type()
	{
		when(accessedInstances.getType()).thenAnswer(new Answer<Object>()
		{
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				return UnsupportedType.class;
			}
		});

		when(differProvider.retrieveDifferForType(eq(UnsupportedType.class))).thenReturn(null);

		differDispatcher.dispatch(DiffNode.ROOT, instances, accessor);
	}

	private static <T> Answer<Class<T>> returnType(final Class<T> type)
	{
		return new Answer<Class<T>>()
		{
			public Class<T> answer(final InvocationOnMock invocation) throws Throwable
			{
				return type;
			}
		};
	}

	private static class UnsupportedType
	{
	}
}
