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

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;

/** @author Daniel Bechler */
public class MapDifferTest
{
	private MapDiffer differ;
	@Mock
	private DifferDelegator delegatingObjectDiffer;
	private Configuration configuration;

	@BeforeMethod
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		configuration = new Configuration();
		differ = new MapDiffer(delegatingObjectDiffer, configuration);
	}

	@Test
	public void testWithoutMapInBaseAndWorking()
	{
		final MapNode node = differ.compare((Map<?, ?>) null, null);
		assertThat(node.getState(), is(Node.State.UNTOUCHED));
		assertThat(node.hasChildren(), is(false));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testConstructionWithoutDelegator()
	{
		new MapDiffer(null, null);
	}

	@Test
	public void testConstructionWithDelegator()
	{
		// just for the coverage
		new MapDiffer(new DifferDelegator(new DifferFactory(new Configuration())), new Configuration());
	}
}
