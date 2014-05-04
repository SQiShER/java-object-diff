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

package de.danielbechler.diff.issues.issue15;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.helper.NodeAssertions;
import de.danielbechler.diff.mock.ObjectWithNestedObject;
import de.danielbechler.diff.node.DiffNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static de.danielbechler.diff.circular.CircularReferenceMatchingMode.EQUALS_METHOD;

/**
 * @author Daniel Bechler
 */
public class CircularReferenceDetectionBasedOnEqualsIT
{
	private ObjectDiffer objectDiffer;

	@BeforeMethod
	public void setUp() throws Exception
	{
		final ObjectDifferBuilder configuration = ObjectDifferBuilder.startBuilding();
		configuration.configure()
				.circularReferenceHandling()
				.matchCircularReferencesUsing(EQUALS_METHOD);
		objectDiffer = configuration.build();
	}

	@Test
	public void detectsCircularReference_whenEncounteringSameObjectTwice() throws Exception
	{
		final ObjectWithNestedObject object = new ObjectWithNestedObject("foo");
		object.setObject(object);
		final DiffNode node = objectDiffer.compare(object, null);
		NodeAssertions.assertThat(node).child("object").isCircular();
	}

	@Test
	public void detectsCircularReference_whenEncounteringDifferentButEqualObjectsTwice() throws Exception
	{
		final ObjectWithNestedObject object = new ObjectWithNestedObject("foo", new ObjectWithNestedObject("foo"));
		final DiffNode node = objectDiffer.compare(object, null);
		NodeAssertions.assertThat(node).child("object").isCircular();
	}
}
