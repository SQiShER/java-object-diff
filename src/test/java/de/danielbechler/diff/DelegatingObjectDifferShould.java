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
public class DelegatingObjectDifferShould
{
	private DelegatingObjectDiffer delegatingObjectDiffer;
	@Mock
	private Node parentNode;
	@Mock
	private Differ differ;
	@Mock
	private DifferFactory differFactory;
	@Mock
	private Instances instances;

	@BeforeMethod
	public void setUp()
	{
		initMocks(this);
		delegatingObjectDiffer = new DelegatingObjectDiffer(new Configuration());
		delegatingObjectDiffer.setDifferFactory(differFactory);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_no_instances_are_passed() throws Exception
	{
		delegatingObjectDiffer.delegate(parentNode, null);
	}

	@Test
	public void delegate_comparison_to_appropriate_differ() throws Exception
	{
		doReturn(Object.class).when(instances).getType();
		doReturn(differ).when(differFactory).createDiffer(Object.class);

		delegatingObjectDiffer.delegate(parentNode, instances);

		verify(differ).compare(parentNode, instances);
	}
}
