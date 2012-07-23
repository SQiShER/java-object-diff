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

import org.junit.*;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

/** @author Daniel Bechler */
public class CircularReferenceDetectorTest
{
	private CircularReferenceDetector circularReferenceDetector;

	@Before
	public void setUp()
	{
		circularReferenceDetector = new CircularReferenceDetector();
	}

	@Test
	public void testIsNew_returns_true_if_nothing_has_been_pushed() throws Exception
	{
		assertTrue(circularReferenceDetector.isNew());
	}

	@Test
	public void testIsNew_returns_false_if_instance_has_been_pushed() throws Exception
	{
		circularReferenceDetector.push("foo");
		assertFalse(circularReferenceDetector.isNew());
	}

	@Test
	public void testIsNew_returns_false_if_instance_has_been_pushed_but_later_removed() throws Exception
	{
		circularReferenceDetector.push("foo");
		circularReferenceDetector.remove("foo");
		assertFalse(circularReferenceDetector.isNew());
	}

	@Test
	public void testPush_does_nothing_with_null_object() throws Exception
	{
		circularReferenceDetector.push(null);
		assertThat(circularReferenceDetector.size(), is(0));
	}

	@Test
	public void testPush_adds_unknown_object_to_stack() throws Exception
	{
		circularReferenceDetector.push("foo");
		assertThat(circularReferenceDetector.size(), is(1));
	}

	@Test(expected = CircularReferenceDetector.CircularReferenceException.class)
	public void testPush_throws_CircularReferenceException_on_known_object() throws Exception
	{
		circularReferenceDetector.push("foo");
		circularReferenceDetector.push("foo");
	}

	@Test
	public void testRemove_does_nothing_with_null_object() throws Exception
	{
		assertThat(circularReferenceDetector.size(), is(0));
		circularReferenceDetector.remove(null);
		assertThat(circularReferenceDetector.size(), is(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemove_throws_IllegalArgumentException_when_trying_to_remove_any_instance_other_than_the_last_pushed_one() throws Exception
	{
		circularReferenceDetector.push("foo");
		circularReferenceDetector.remove("bar");
	}

	@Test
	public void testRemove_removes_instance_when_it_was_the_last_one_pushed() throws Exception
	{
		circularReferenceDetector.push("foo");
		assertThat(circularReferenceDetector.size(), is(1));
		circularReferenceDetector.remove("foo");
		assertThat(circularReferenceDetector.size(), is(0));
	}

	@Test
	public void testKnows_returns_true_for_previously_added_instance()
	{
		circularReferenceDetector.push("foo");
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
		circularReferenceDetector.push("foo");
		circularReferenceDetector.remove("foo");
		assertFalse(circularReferenceDetector.knows("foo"));
	}
}
