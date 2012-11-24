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
import org.mockito.*;
import org.testng.annotations.*;

import static de.danielbechler.diff.node.NodeAssertions.*;
import static org.mockito.Mockito.*;

/** @author Daniel Bechler */
public class BeanDifferShould
{
	private BeanDiffer differ;
	@Mock
	private DelegatingObjectDiffer delegatingObjectDiffer;
	@Mock
	private Node node;
	@Mock
	private Configuration configuration;

	@BeforeMethod
	public void setUp() throws Exception
	{
		delegatingObjectDiffer = mock(DelegatingObjectDiffer.class);
		configuration = mock(Configuration.class);
		differ = new BeanDiffer(delegatingObjectDiffer, configuration);
	}

	@Test
	public void detect_added_bean()
	{
		final Node node = differ.compare(new Object(), null);

		assertThat(node).self().hasState(Node.State.ADDED);
	}

//	@Test
//	public void detect_removed_bean()
//	{
//		final Node node = differ.compare(null, new Object());
//
//		assertThat(node).self().hasState(Node.State.REMOVED);
//	}
}
