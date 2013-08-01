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

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.accessor.exception.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import org.mockito.Mock;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import org.testng.annotations.*;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
public class DifferDelegatorShould
{
	@Mock
	private DifferFactory differFactory;
	@Mock
	private Accessor accessor;
	@Mock
	private CircularReferenceDetectorFactory circularReferenceDetectorFactory;
	@Mock
	private CircularReferenceDetector circularReferenceDetector;
	@Mock
	private ExceptionListener exceptionListener;
	@Mock
	private Configuration configuration;
	private Instances instances;
	private DifferDelegator differDelegator;

	@BeforeMethod
	public void setUp() throws Exception
	{
		initMocks(this);

		when(circularReferenceDetectorFactory.create()).thenReturn(circularReferenceDetector);
		when(configuration.getExceptionListener()).thenReturn(exceptionListener);

		differDelegator = new DifferDelegator(differFactory, circularReferenceDetectorFactory, configuration);
	}

	@SuppressWarnings("unchecked")
	private void given_the_delegated_node_is_circular(final PropertyPath circularStartPath)
	{
		instances = mock(Instances.class);
		differDelegator = new DifferDelegator(differFactory, circularReferenceDetectorFactory, configuration)
		{
			@Override
			protected void rememberInstances(final Node parentNode, final Instances instances)
			{
				throw new CircularReferenceDetector.CircularReferenceException(circularStartPath);
			}
		};
		when(instances.getSourceAccessor()).thenReturn(RootAccessor.getInstance());
		when(instances.getType()).then(returnType(Object.class));
		when(differFactory.createDiffer(ObjectWithCircularReference.class, differDelegator)).thenReturn(mock(Differ.class));
	}

	@Test
	public void assign_the_circular_start_path_if_the_delegated_node_is_circular() throws Exception
	{
		final PropertyPath circularStartPath = PropertyPath.buildRootPath();
		given_the_delegated_node_is_circular(circularStartPath);

		final Node node = differDelegator.delegate(Node.ROOT, instances);

		assertThat(node.getCircleStartPath()).isEqualTo(circularStartPath);
	}

	@Test
	public void mark_node_as_circular_if_the_delegated_node_is_circular() throws Exception
	{
		given_the_delegated_node_is_circular(PropertyPath.buildRootPath());

		final Node node = differDelegator.delegate(Node.ROOT, instances);

		assertThat(node.getState()).isEqualTo(Node.State.CIRCULAR);
	}

	@Test
	public void pass_node_to_onCircularReferenceException_method_of_the_exceptionListener_if_the_delegated_node_is_circular() throws Exception
	{
		given_the_delegated_node_is_circular(PropertyPath.buildRootPath());

		final Node node = differDelegator.delegate(Node.ROOT, instances);

		verify(exceptionListener).onCircularReferenceException(node);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void throw_exception_if_no_differ_can_be_found_for_instance_type()
	{
		instances = Instances.of(RootAccessor.getInstance(), new UnsupportedType(), null);

		when(differFactory.createDiffer(eq(UnsupportedType.class), same(differDelegator))).thenReturn(null);

		differDelegator.delegate(Node.ROOT, instances);
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

	private static class UnsupportedType {
	}
}
