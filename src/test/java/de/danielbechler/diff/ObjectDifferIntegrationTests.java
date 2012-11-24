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

import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import org.fest.assertions.api.*;
import org.testng.annotations.*;

import java.util.*;

import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;

/** @author Daniel Bechler */
@Test(groups = TestGroups.INTEGRATION)
public class ObjectDifferIntegrationTests
{
	private ObjectDiffer objectDiffer;

	@BeforeMethod
	public void setUp() throws Exception
	{
		objectDiffer = ObjectDifferFactory.getInstance();
	}

	public void testCompareBeanWithIgnoredMapProperty()
	{
		final ObjectWithIgnoredMap working = new ObjectWithIgnoredMap();
		working.getMap().put("foo", "bar");

		final Node node = objectDiffer.compare(working, new ObjectWithIgnoredMap());

		NodeAssertions.assertThat(node).self().hasState(Node.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	public void testCompareCollectionWithIgnoredCollectionProperty()
	{
		final ObjectWithCollection working = new ObjectWithCollection();
		working.getCollection().add("foo");
		objectDiffer.getConfiguration().withoutProperty(PropertyPath.buildWith("collection"));

		final Node node = objectDiffer.compare(working, new ObjectWithCollection());

		NodeAssertions.assertThat(node).self().hasState(Node.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	public void testCompareCollectionWithAddedItem() throws Exception
	{
		final Collection<String> working = new LinkedList<String>(asList("foo"));
		final Collection<String> base = new LinkedList<String>();

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(Node.State.CHANGED);
		NodeAssertions.assertThat(node).child(new CollectionElement("foo")).hasState(Node.State.ADDED);
	}

	public void testCompareCollectionWithRemovedItem() throws Exception
	{
		final Collection<String> working = new LinkedList<String>();
		final Collection<String> base = new LinkedList<String>(asList("foo"));

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(Node.State.CHANGED);
		NodeAssertions.assertThat(node).child(new CollectionElement("foo")).hasState(Node.State.REMOVED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testThatIgnoredPropertiesAreNeverAccessed()
	{
		final ObjectWithAccessTrackingIgnoredProperty working = new ObjectWithAccessTrackingIgnoredProperty();
		final ObjectWithAccessTrackingIgnoredProperty base = new ObjectWithAccessTrackingIgnoredProperty();

		ObjectDifferFactory.getInstance().compare(working, base);

		Assertions.assertThat(working.accessed).isFalse();
		Assertions.assertThat(base.accessed).isFalse();
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testThatObjectGraphForAddedObjectsGetsReturned()
	{
		final ObjectWithNestedObject base = new ObjectWithNestedObject("1");
		final ObjectWithNestedObject working = new ObjectWithNestedObject("1", new ObjectWithNestedObject("2", new ObjectWithNestedObject("foo")));
		final Configuration configuration = new Configuration().withChildrenOfAddedNodes();

		final Node node = ObjectDifferFactory.getInstance(configuration).compare(working, base);

		node.visit(new NodeHierarchyVisitor());
		NodeAssertions.assertThat(node).root().hasState(Node.State.CHANGED);
		NodeAssertions.assertThat(node).child("object").hasState(Node.State.ADDED);
		NodeAssertions.assertThat(node).child("object", "object").hasState(Node.State.ADDED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testCompareCollectionWithDifferentCollectionImplementationsSucceeds() throws Exception
	{
		final Collection<String> base = new LinkedHashSet<String>(asList("one", "two"));
		final Collection<String> working = new TreeSet<String>(asList("one", "three"));

		final Node node = ObjectDifferFactory.getInstance().compare(base, working);

		NodeAssertions.assertThat(node).self().hasState(Node.State.CHANGED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testCompareCollectionWithChangedItem() throws Exception
	{
		final List<?> working = asList(new ObjectWithIdentityAndValue("foo", "1"));
		final List<?> base = asList(new ObjectWithIdentityAndValue("foo", "2"));

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		assertThat(node.hasChanges(), is(true));

		NodeAssertions.assertThat(node)
					  .child(new CollectionElement(new ObjectWithIdentityAndValue("foo")))
					  .hasState(Node.State.CHANGED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithNewMapInWorkingAndNoneInBase()
	{
		final Map<String, String> base = null;
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(Node.State.ADDED);
		NodeAssertions.assertThat(node).child(new MapElement("foo")).hasState(Node.State.ADDED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithAdditionOfSimpleTypeToWorkingMap()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(Node.State.CHANGED);
		NodeAssertions.assertThat(node).child(new MapElement("foo")).hasState(Node.State.ADDED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithSameEntryInBaseAndWorking()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		base.put("foo", "bar");
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(Node.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithSingleEntryAddedToWorkingMap()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		base.put("foo", "bar");
		final Map<String, String> working = null;

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(Node.State.REMOVED);
		NodeAssertions.assertThat(node).child(new MapElement("foo")).hasState(Node.State.REMOVED);
	}

	/**
	 * Ensures that the map can handle null values in both, the base and the working version, in which case no
	 * type can be detected.
	 */
	@Test(groups = TestGroups.INTEGRATION)
	public void testWithAllNullMapItem()
	{
		final Map<String, String> working = new HashMap<String, String>(1);
		working.put("foo", null);

		final Map<String, String> base = new HashMap<String, String>(1);
		base.put("foo", null);

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(Node.State.UNTOUCHED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithSameEntries()
	{
		final Map<String, String> modified = new LinkedHashMap<String, String>(1);
		modified.put("foo", "bar");
		final Map<String, String> base = new LinkedHashMap<String, String>(modified);
		modified.put("ping", "pong");

		final Node node = ObjectDifferFactory.getInstance().compare(modified, base);

		NodeAssertions.assertThat(node).root().hasChildren(1);
		NodeAssertions.assertThat(node)
					  .child(PropertyPath.createBuilder().withRoot().withMapKey("foo"))
					  .doesNotExist();
		NodeAssertions.assertThat(node).child(PropertyPath.createBuilder().withRoot().withMapKey("ping"))
					  .hasState(Node.State.ADDED)
					  .hasNoChildren();
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithChangedEntry()
	{
		final Map<String, String> working = new LinkedHashMap<String, String>(1);
		working.put("foo", "bar");

		final Map<String, String> base = new LinkedHashMap<String, String>(1);
		base.put("foo", "woot");

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);
		NodeAssertions.assertThat(node).self().hasChildren(1);
		NodeAssertions.assertThat(node).child(new MapElement("foo")).hasState(Node.State.CHANGED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testCompareWithDifferentMapImplementationsSucceeds() throws Exception
	{
		final Map<String, String> base = new LinkedHashMap<String, String>(Collections.singletonMap("test", "foo"));
		final Map<String, String> working = new TreeMap<String, String>(Collections.singletonMap("test", "bar"));

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(Node.State.CHANGED);
	}
}
