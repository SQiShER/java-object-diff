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

package de.danielbechler.diff.integration.graph;

import de.danielbechler.diff.Configuration;
import de.danielbechler.diff.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import org.testng.annotations.*;

/** @author Daniel Bechler */
public class CircularReferenceDetectionBasedOnIdentityTest
{
	private ObjectDiffer objectDiffer;

	@BeforeMethod
	public void setUp() throws Exception
	{
		final Configuration configuration = new Configuration();
		configuration.withChildrenOfAddedNodes();
		objectDiffer = ObjectDifferFactory.getInstance(configuration);
	}

	@Test
	public void detectsCircularReference_whenEncounteringSameObjectTwice() throws Exception
	{
		final ObjectWithNestedObject object = new ObjectWithNestedObject("foo");
		object.setObject(object);
		final Node node = objectDiffer.compare(object, null);
		NodeAssertions.assertThat(node).child("object").isCircular();
	}

	@Test
	public void detectsNoCircularReference_whenEncounteringDifferentButEqualObjectsTwice() throws Exception
	{
		final ObjectWithNestedObject object = new ObjectWithNestedObject("foo", new ObjectWithNestedObject("foo"));
		final Node node = objectDiffer.compare(object, null);
		NodeAssertions.assertThat(node).child("object").hasState(Node.State.ADDED);
	}
}
