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

package de.danielbechler.diff;

import de.danielbechler.diff.helper.NodeAssertions;
import de.danielbechler.diff.helper.TestGroups;
import de.danielbechler.diff.mock.ObjectWithAccessTrackingIgnoredProperty;
import de.danielbechler.diff.mock.ObjectWithCollection;
import de.danielbechler.diff.mock.ObjectWithHashCodeAndEquals;
import de.danielbechler.diff.mock.ObjectWithIdentityAndValue;
import de.danielbechler.diff.mock.ObjectWithIgnoredMap;
import de.danielbechler.diff.mock.ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection;
import de.danielbechler.diff.mock.ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap;
import de.danielbechler.diff.mock.ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject;
import de.danielbechler.diff.mock.ObjectWithNestedObject;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.NodeHierarchyVisitor;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.selector.CollectionItemElementSelector;
import de.danielbechler.diff.selector.MapKeyElementSelector;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static de.danielbechler.diff.helper.NodeAssertions.assertThat;
import static de.danielbechler.diff.helper.TestGroups.INTEGRATION;
import static java.util.Arrays.asList;

/**
 * @author Daniel Bechler
 */
@Test(groups = TestGroups.INTEGRATION)
public class ObjectDifferIT
{
	private ObjectDiffer objectDiffer;

	@BeforeMethod
	public void setUp() throws Exception
	{
		objectDiffer = ObjectDifferBuilder.buildDefault();
	}

	public void testCompareBeanWithIgnoredMapProperty()
	{
		final ObjectWithIgnoredMap working = new ObjectWithIgnoredMap();
		working.getMap().put("foo", "bar");

		final DiffNode node = objectDiffer.compare(working, new ObjectWithIgnoredMap());

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	public void testCompareCollectionWithIgnoredCollectionProperty()
	{
		final ObjectWithCollection working = new ObjectWithCollection();
		working.getCollection().add("foo");
		final ObjectDifferBuilder objectDifferBuilder = ObjectDifferBuilder.startBuilding();
		objectDifferBuilder.inclusion()
				.exclude()
				.node(NodePath.with("collection"));
		objectDiffer = objectDifferBuilder.build();

		final DiffNode node = objectDiffer.compare(working, new ObjectWithCollection());

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	public void testCompareCollectionWithAddedItem() throws Exception
	{
		final Collection<String> working = new LinkedList<String>(asList("foo"));
		final Collection<String> base = new LinkedList<String>();

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.CHANGED);
		NodeAssertions.assertThat(node).child(new CollectionItemElementSelector("foo")).hasState(DiffNode.State.ADDED);
	}

	public void testCompareCollectionWithRemovedItem() throws Exception
	{
		final Collection<String> working = new LinkedList<String>();
		final Collection<String> base = new LinkedList<String>(asList("foo"));

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.CHANGED);
		NodeAssertions.assertThat(node).child(new CollectionItemElementSelector("foo")).hasState(DiffNode.State.REMOVED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testThatIgnoredPropertiesAreNeverAccessed()
	{
		final ObjectWithAccessTrackingIgnoredProperty working = new ObjectWithAccessTrackingIgnoredProperty();
		final ObjectWithAccessTrackingIgnoredProperty base = new ObjectWithAccessTrackingIgnoredProperty();

		ObjectDifferBuilder.buildDefault().compare(working, base);

		Assertions.assertThat(working.accessed).isFalse();
		Assertions.assertThat(base.accessed).isFalse();
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testThatObjectGraphForAddedObjectsGetsReturned()
	{
		final ObjectWithNestedObject base = new ObjectWithNestedObject("1");
		final ObjectWithNestedObject working = new ObjectWithNestedObject("1", new ObjectWithNestedObject("2", new ObjectWithNestedObject("foo")));
		final ObjectDifferBuilder objectDifferBuilder = ObjectDifferBuilder.startBuilding();
		// final Configuration2 objectDifferBuilder = new Configuration2().withChildrenOfAddedNodes();

		final DiffNode node = objectDifferBuilder.build().compare(working, base);

		node.visit(new NodeHierarchyVisitor());
		NodeAssertions.assertThat(node).root().hasState(DiffNode.State.CHANGED);
		NodeAssertions.assertThat(node).child("object").hasState(DiffNode.State.ADDED);
		NodeAssertions.assertThat(node).child("object", "object").hasState(DiffNode.State.ADDED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testCompareCollectionWithDifferentCollectionImplementationsSucceeds() throws Exception
	{
		final Collection<String> base = new LinkedHashSet<String>(asList("one", "two"));
		final Collection<String> working = new TreeSet<String>(asList("one", "three"));

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(base, working);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.CHANGED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testCompareCollectionWithChangedItem() throws Exception
	{
		final List<?> working = asList(new ObjectWithIdentityAndValue("foo", "1"));
		final List<?> base = asList(new ObjectWithIdentityAndValue("foo", "2"));

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		assertThat(node).self().hasChanges();
		assertThat(node).child(new CollectionItemElementSelector(new ObjectWithIdentityAndValue("foo")))
				.hasState(DiffNode.State.CHANGED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithNewMapInWorkingAndNoneInBase()
	{
		final Map<String, String> base = null;
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.ADDED);
		NodeAssertions.assertThat(node).child(new MapKeyElementSelector("foo")).hasState(DiffNode.State.ADDED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithAdditionOfSimpleTypeToWorkingMap()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.CHANGED);
		NodeAssertions.assertThat(node).child(new MapKeyElementSelector("foo")).hasState(DiffNode.State.ADDED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithSameEntryInBaseAndWorking()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		base.put("foo", "bar");
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithSingleEntryAddedToWorkingMap()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		base.put("foo", "bar");
		final Map<String, String> working = null;

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.REMOVED);
		NodeAssertions.assertThat(node).child(new MapKeyElementSelector("foo")).hasState(DiffNode.State.REMOVED);
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

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.UNTOUCHED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithSameEntries()
	{
		final Map<String, String> modified = new LinkedHashMap<String, String>(1);
		modified.put("foo", "bar");
		final Map<String, String> base = new LinkedHashMap<String, String>(modified);
		modified.put("ping", "pong");

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(modified, base);

		NodeAssertions.assertThat(node).root().hasChildren(1);
		NodeAssertions.assertThat(node)
				.child(NodePath.startBuilding().mapKey("foo"))
				.doesNotExist();
		NodeAssertions.assertThat(node).child(NodePath.startBuilding().mapKey("ping"))
				.hasState(DiffNode.State.ADDED)
				.hasNoChildren();
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testWithChangedEntry()
	{
		final Map<String, String> working = new LinkedHashMap<String, String>(1);
		working.put("foo", "bar");

		final Map<String, String> base = new LinkedHashMap<String, String>(1);
		base.put("foo", "woot");

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);
		NodeAssertions.assertThat(node).self().hasChildren(1);
		NodeAssertions.assertThat(node).child(new MapKeyElementSelector("foo")).hasState(DiffNode.State.CHANGED);
	}

	@Test(groups = TestGroups.INTEGRATION)
	public void testCompareWithDifferentMapImplementationsSucceeds() throws Exception
	{
		final Map<String, String> base = new LinkedHashMap<String, String>(Collections.singletonMap("test", "foo"));
		final Map<String, String> working = new TreeMap<String, String>(Collections.singletonMap("test", "bar"));

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.CHANGED);
	}

	@Test(groups = INTEGRATION)
	public void testCompareWithDifferentStrings() throws Exception
	{
		final DiffNode node = ObjectDifferBuilder.buildDefault().compare("foo", "bar");

		assertThat(node).self().hasState(DiffNode.State.CHANGED);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, groups = INTEGRATION)
	public void testCompareWithDifferentTypes()
	{
		ObjectDifferBuilder.buildDefault().compare("foo", 1337);
	}

	@Test(groups = INTEGRATION)
	public void testCompareWithIgnoredProperty()
	{
		objectDiffer = ObjectDifferBuilder.startBuilding()
				.inclusion()
				.exclude().node(NodePath.with("value")).and()
				.filtering().returnNodesWithState(DiffNode.State.IGNORED).and()
				.build();

		final ObjectWithIdentityAndValue working = new ObjectWithIdentityAndValue("1", "foo");
		final ObjectWithIdentityAndValue base = new ObjectWithIdentityAndValue("1", "bar");
		final DiffNode node = objectDiffer.compare(working, base);

		NodeAssertions.assertThat(node).child("value").hasState(DiffNode.State.IGNORED);
	}

	@Test(groups = INTEGRATION)
	public void testCompareWithComplexType()
	{
//		when(introspector.introspect(any(Class.class))).thenReturn(Arrays.<Accessor>asList(accessor));
//		when(delegate.delegate(any(Node.class), any(Instances.class))).thenReturn(node);
//		when(configuration.isIntrospectible(any(Node.class))).thenReturn(true);
//		when(configuration.isReturnable(any(Node.class))).thenReturn(true);
//		when(node.hasChanges()).thenReturn(true);

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(
				new ObjectWithIdentityAndValue("a", "1"),
				new ObjectWithIdentityAndValue("a", "2"));

		assertThat(node).self().hasState(DiffNode.State.CHANGED);
	}

	@Test(enabled = false, description = "Currently this is simply not possible because of the way, the CollectionItemAccessor works. Would be great, to support this.")
	public void testCompareWithListContainingObjectTwiceDetectsIfOneGetsRemoved() throws Exception
	{
		final List<ObjectWithHashCodeAndEquals> base = asList(new ObjectWithHashCodeAndEquals("foo"), new ObjectWithHashCodeAndEquals("foo"));
		final List<ObjectWithHashCodeAndEquals> working = asList(new ObjectWithHashCodeAndEquals("foo"));
		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);
		node.visit(new NodeHierarchyVisitor());
		assertThat(node)
				.child(NodePath.startBuilding()
						.collectionItem(new ObjectWithHashCodeAndEquals("foo"))
						.build())
				.hasState(DiffNode.State.REMOVED);
	}

	@Test(enabled = false, description = "Currently this is simply not possible because of the way, the CollectionItemAccessor works. Would be great, to support this.")
	public void testCompareWithListContainingObjectOnceDetectsIfAnotherInstanceOfItGetsAdded() throws Exception
	{
		final List<ObjectWithHashCodeAndEquals> base = asList(new ObjectWithHashCodeAndEquals("foo"));
		final List<ObjectWithHashCodeAndEquals> working = asList(new ObjectWithHashCodeAndEquals("foo"), new ObjectWithHashCodeAndEquals("foo"));
		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);
		node.visit(new NodeHierarchyVisitor());
		assertThat(node).child(NodePath.startBuilding()
				.collectionItem(new ObjectWithHashCodeAndEquals("foo"))
				.build()).hasState(DiffNode.State.ADDED);
	}

	public void testCompareBeanWithEqualsOnlyValueProviderMethodOnGetCollectionPropertyNoChangeInMethodResult()
	{
		final List<String> forWorking = new ArrayList<String>();
		forWorking.add("one");
		final List<String> forBase = new ArrayList<String>();
		forBase.add("uno");
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(forWorking);
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(forBase);

		final DiffNode node = objectDiffer.compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	public void testCompareBeanWithEqualOnlyValueProviderMethodOnGetCollectionPropertyWithChangeInMethodResult()
	{
		final List<String> forWorking = new ArrayList<String>();
		forWorking.add("one");
		forWorking.add("two");
		final List<String> forBase = new ArrayList<String>();
		forBase.add("uno");
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(forWorking);
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(forBase);

		final DiffNode node = objectDiffer.compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.CHANGED);
		assertThat(node)
				.child(NodePath.with("collection"))
				.hasState(DiffNode.State.CHANGED);
	}

	public void testCompareBeanWithEqualsOnlyValueProviderMethodOnGetObjectPropertyNoChangeInMethodResult()
	{
		final ObjectWithNestedObject forWorking = new ObjectWithNestedObject("childid");
		forWorking.setObject(new ObjectWithNestedObject("grandchildid"));
		final ObjectWithNestedObject forBase = new ObjectWithNestedObject("childid");
		forBase.setObject(new ObjectWithNestedObject("differentgrandchildid"));
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject("id", forWorking);
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject("id", forBase);

		final DiffNode node = objectDiffer.compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	public void testCompareBeanWithEqualsOnlyValueProviderMethodOnGetObjectPropertyWithChangeInMethodResult()
	{
		final ObjectWithNestedObject forWorking = new ObjectWithNestedObject("childid");
		forWorking.setObject(new ObjectWithNestedObject("grandchildid"));
		final ObjectWithNestedObject forBase = new ObjectWithNestedObject("differentchildid");
		forBase.setObject(new ObjectWithNestedObject("differentgrandchildid"));
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject("id", forWorking);
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject("id", forBase);

		final DiffNode node = objectDiffer.compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.CHANGED);
		assertThat(node)
				.child(NodePath.with("object"))
				.hasState(DiffNode.State.CHANGED);
	}

	public void testCompareBeanWithEqualsOnlyValueProviderMethodOnGetMapPropertyNoChangeInMethodResult()
	{
		final Map<String, String> forWorking = new HashMap<String, String>();
		forWorking.put("key1", "val1");
		final Map<String, String> forBase = new HashMap<String, String>();
		forBase.put("keyone", "valone");
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap(forWorking);
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap(forBase);

		final DiffNode node = objectDiffer.compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.UNTOUCHED);
		NodeAssertions.assertThat(node).self().hasNoChildren();
	}

	public void testCompareBeanWithEqualsOnlyValueProviderMethodOnGetMapPropertyWithChangeInMethodResult()
	{
		final Map<String, String> forWorking = new HashMap<String, String>();
		forWorking.put("key1", "val1");
		forWorking.put("key2", "val2");
		final Map<String, String> forBase = new HashMap<String, String>();
		forBase.put("keyone", "valone");
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap(forWorking);
		final ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap(forBase);

		final DiffNode node = objectDiffer.compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.CHANGED);
		assertThat(node)
				.child(NodePath.with("map"))
				.hasState(DiffNode.State.CHANGED);
	}
}
