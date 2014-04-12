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

package de.danielbechler.diff.config.circular;

import de.danielbechler.diff.mock.ObjectWithString;
import de.danielbechler.diff.path.NodePath;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Daniel Bechler
 */
public class CircularReferenceDetectorTest
{
	private CircularReferenceDetector circularReferenceDetector;

	@BeforeMethod
	public void setUp()
	{
		circularReferenceDetector = new CircularReferenceDetector(CircularReferenceDetector.ReferenceMatchingMode.EQUALITY_OPERATOR);
	}

	@Test
	public void testPush_does_nothing_with_null_object() throws Exception
	{
		circularReferenceDetector.push(null, null);
		assertThat(circularReferenceDetector.size(), is(0));
	}

	@Test
	public void testPush_adds_unknown_object_to_stack() throws Exception
	{
		circularReferenceDetector.push("foo", NodePath.withRoot());
		assertThat(circularReferenceDetector.size(), is(1));
	}

	@Test
	public void testPush_throws_CircularReferenceException_on_known_object() throws Exception
	{
		circularReferenceDetector.push("foo", NodePath.withRoot());
		try
		{
			circularReferenceDetector.push("foo", NodePath.startBuilding()
					.propertyName("test")
					.build());
			Assertions.fail("Expected CircularReferenceException wasn't thrown.");
		}
		catch (CircularReferenceDetector.CircularReferenceException e)
		{
			final NodePath nodePath = e.getNodePath();
			assertThat(nodePath).isEqualTo(NodePath.withRoot());
		}
	}

	@Test
	public void testRemove_does_nothing_with_null_object() throws Exception
	{
		assertThat(circularReferenceDetector.size(), is(0));
		circularReferenceDetector.remove(null);
		assertThat(circularReferenceDetector.size(), is(0));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testRemove_throws_IllegalArgumentException_when_trying_to_remove_any_instance_other_than_the_last_pushed_one() throws Exception
	{
		circularReferenceDetector.push("foo", null);
		circularReferenceDetector.remove("bar");
	}

	@Test
	public void testRemove_removes_instance_when_it_was_the_last_one_pushed() throws Exception
	{
		circularReferenceDetector.push("foo", null);
		assertThat(circularReferenceDetector.size(), is(1));
		circularReferenceDetector.remove("foo");
		assertThat(circularReferenceDetector.size(), is(0));
	}

	@Test
	public void testKnows_returns_true_for_previously_added_instance()
	{
		circularReferenceDetector.push("foo", null);
		assertTrue(circularReferenceDetector.knows("foo"));
	}

	@Test
	public void testKnows_returns_false_if_instance_has_not_been_pushed()
	{
		assertFalse(circularReferenceDetector.knows("foo"));
	}

	@Test
	public void testKnows_returns_false_if_instance_has_been_pushed_but_later_removed()
	{
		circularReferenceDetector.push("foo", null);
		circularReferenceDetector.remove("foo");
		assertFalse(circularReferenceDetector.knows("foo"));
	}

	@Test
	public void knowsEqualObject_whenReferenceMatchingModeIsSetToEqualsMethod()
	{
		circularReferenceDetector = new CircularReferenceDetector(CircularReferenceDetector.ReferenceMatchingMode.EQUALS_METHOD);
		circularReferenceDetector.push(new ObjectWithString("foo"), NodePath.withRoot());
		assertThat(circularReferenceDetector.knows(new ObjectWithString("foo"))).isTrue();
	}

	@Test
	public void removesEqualObject_whenReferenceMatchingModeIsSetToEqualsMethod()
	{
		circularReferenceDetector = new CircularReferenceDetector(CircularReferenceDetector.ReferenceMatchingMode.EQUALS_METHOD);
		circularReferenceDetector.push(new ObjectWithString("foo"), NodePath.withRoot());
		circularReferenceDetector.remove(new ObjectWithString("foo"));
		assertThat(circularReferenceDetector.size()).isEqualTo(0);
	}

	@Test(expectedExceptions = CircularReferenceDetector.CircularReferenceException.class)
	public void throwsException_onAttemptToPushEqualObject_whenReferenceMatchingModeIsSetToEqualsMethod()
	{
		circularReferenceDetector = new CircularReferenceDetector(CircularReferenceDetector.ReferenceMatchingMode.EQUALS_METHOD);
		circularReferenceDetector.push(new ObjectWithString("foo"), NodePath.withRoot());
		circularReferenceDetector.push(new ObjectWithString("foo"), NodePath.withRoot());
	}
}
