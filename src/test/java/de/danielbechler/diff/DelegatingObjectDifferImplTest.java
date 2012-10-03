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
import org.testng.annotations.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
public class DelegatingObjectDifferImplTest
{
	private DelegatingObjectDifferImpl delegatingObjectDiffer;
	private Node parentNode;

	@Mock
	private Differ primitiveDiffer;

	@Mock
	private Instances instances;

	@BeforeMethod
	public void setUp()
	{
		initMocks(this);
		parentNode = Node.ROOT;
		delegatingObjectDiffer = new DelegatingObjectDifferImpl(null, null, null, primitiveDiffer);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testDelegateThrowsExceptionWhenInstancesAreNotPassed() throws Exception
	{
		delegatingObjectDiffer.delegate(parentNode, null);
	}

	@Test
	public void testDelegatesPrimitiveTypesToPrimitiveDiffer() throws Exception
	{
		when(instances.isPrimitiveType()).thenReturn(true);

		delegatingObjectDiffer.delegate(parentNode, instances);

		verify(primitiveDiffer, times(1)).compare(eq(parentNode), eq(instances));
	}
}
