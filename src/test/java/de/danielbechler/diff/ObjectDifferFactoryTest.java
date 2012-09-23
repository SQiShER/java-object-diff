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

import org.hamcrest.core.*;
import org.testng.annotations.*;

import static org.hamcrest.MatcherAssert.*;

/** @author Daniel Bechler */
public class ObjectDifferFactoryTest
{
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetInstanceWithNullConfiguration() throws Exception
	{
		ObjectDifferFactory.getInstance(null);
	}

	@Test
	public void testGetInstanceWithConfiguration() throws Exception
	{
		final Configuration configuration = new Configuration();
		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance(configuration);
		assertThat(objectDiffer.getConfiguration(), IsEqual.equalTo(configuration));
	}

	@Test
	public void testGetInstance() throws Exception
	{
		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		assertThat(objectDiffer, IsNull.notNullValue());
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testConstruction()
	{
		new ObjectDifferFactory();
	}
}
