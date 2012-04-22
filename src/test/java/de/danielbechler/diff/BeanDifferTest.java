package de.danielbechler.diff;

import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import org.hamcrest.core.*;
import org.junit.*;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNull.*;
import static org.hamcrest.core.IsSame.*;
import static org.junit.Assert.*;

/** @author Daniel Bechler */
public class BeanDifferTest
{
	private ObjectDiffer objectDiffer;

	@Before
	public void setUp()
	{
		objectDiffer = new BeanDiffer();
	}

	@After
	public void tearDown()
	{
		objectDiffer = null;
	}

	@Test
	public void testRootLevelString()
	{
		final String working = "bar";
		final String base = "foo";
		final DiffNode<String> node = objectDiffer.compare(working, base);
		assertThat(node.isRoot(), is(true));
		assertThat(node.getType(), is(DifferenceType.CHANGED));
		assertThat(node.hasChildren(), is(false));
		assertThat(node.getAccessor().get(working), is(sameInstance(working)));
		assertThat(node.getAccessor().get(base), is(sameInstance(base)));
	}

	@Test
	public void testRootLevelStringAdd()
	{
		final DiffNode<String> node = objectDiffer.compare("foo", null);
		assertThat(node.isRoot(), is(true));
		assertThat(node.hasChildren(), is(false));
		assertThat(node.getType(), is(DifferenceType.ADDED));
		assertThat(node.getAccessor().get("foo"), is(sameInstance("foo")));
		assertThat(node.getAccessor().get(null), is(nullValue()));
	}

//	@Test
//	public void testSimpleMapItemAdd()
//	{
//		final ObjectWithMap base = new ObjectWithMap(getMap());
//		final ObjectWithMap working = new ObjectWithMap(getMap());
//		working.getMap().put("foo", "bar");
//		final DiffNode<ObjectWithMap> node = objectDiffer.compare(working, base);
//		assertThat(node.hasChildren(), is(true));
//		final Collection<DiffNode<?>> children = node.getChildren();
//	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testPrimitiveAdd()
	{
		final TestObject base = new TestObject();
		final TestObject modified = new TestObject();
		modified.setNumber(23);
		final DiffNode<Integer> difference = (DiffNode<Integer>) new BeanDiffer()
				.compare(modified, base)
				.getChild(PropertyPathBuilder.pathOf("number"));
		assertThat(difference.getType(), is(equalTo(DifferenceType.ADDED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testPrimitiveRemove()
	{
		final TestObject base = new TestObject();
		base.setNumber(23);
		final TestObject modified = new TestObject();
		final DiffNode<Integer> difference = (DiffNode<Integer>) new BeanDiffer()
				.compare(modified, base)
				.getChild(PropertyPathBuilder.pathOf("number"));
		assertThat(difference.getType(), is(equalTo(DifferenceType.REMOVED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testPrimitiveChange()
	{
		final TestObject base = new TestObject();
		base.setNumber(23);
		final TestObject modified = new TestObject();
		modified.setNumber(42);
		final DiffNode<Integer> difference = (DiffNode<Integer>) new BeanDiffer()
				.compare(modified, base)
				.getChild(PropertyPathBuilder.pathOf("number"));
		assertThat(difference.getType(), Is.is(equalTo(DifferenceType.CHANGED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testObjectAdd()
	{
		final TestObject base = new TestObject();
		final TestObject modified = new TestObject();
		modified.setText("foo");
		final DiffNode<Integer> difference = (DiffNode<Integer>) new BeanDiffer()
				.compare(modified, base)
				.getChild(PropertyPathBuilder.pathOf("text"));
		assertThat(difference.getType(), Is.is(equalTo(DifferenceType.ADDED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testObjectChange()
	{
		final TestObject base = new TestObject();
		base.setText("foo");
		final TestObject modified = new TestObject();
		modified.setText("bar");
		final DiffNode<Integer> difference = (DiffNode<Integer>) new BeanDiffer()
				.compare(modified, base)
				.getChild(PropertyPathBuilder.pathOf("text"));
		assertThat(difference.getType(), Is.is(equalTo(DifferenceType.CHANGED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testObjectRemove()
	{
		final TestObject base = new TestObject();
		base.setText("foo");
		final TestObject modified = new TestObject();
		final DiffNode<Integer> difference = (DiffNode<Integer>) new BeanDiffer()
				.compare(modified, base)
				.getChild(PropertyPathBuilder.pathOf("text"));
		assertThat(difference.getType(), Is.is(equalTo(DifferenceType.REMOVED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testCollectionItemAdd()
	{
		final TestObject base = new TestObject();
		final TestObject modified = new TestObject();
		final NestableCollectionSafeObject item = new NestableCollectionSafeObject("foo");
		item.setValue("meh");
		modified.getCollection().add(item);
		final PropertyPath path = new PropertyPathBuilder()
				.root()
				.propertyName("collection")
				.collectionItem(new NestableCollectionSafeObject("foo"))
				.toPropertyPath();
		final DiffNode<NestableCollectionSafeObject> difference = (DiffNode<NestableCollectionSafeObject>) new BeanDiffer()
				.compare(modified, base)
				.getChild(path);
		assertThat(difference.getType(), Is.is(equalTo(DifferenceType.ADDED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testCollectionItemRemove()
	{
		final TestObject base = new TestObject();
		final NestableCollectionSafeObject item = new NestableCollectionSafeObject("foo");
		item.setValue("meh");
		base.getCollection().add(item);
		final TestObject modified = new TestObject();
		final PropertyPath path = new PropertyPathBuilder()
				.root()
				.propertyName("collection")
				.collectionItem(new NestableCollectionSafeObject("foo"))
				.toPropertyPath();
		final DiffNode<NestableCollectionSafeObject> difference = (DiffNode<NestableCollectionSafeObject>) new BeanDiffer()
				.compare(modified, base)
				.getChild(path);
		assertThat(difference.getType(), Is.is(equalTo(DifferenceType.REMOVED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testCollectionItemChange()
	{
		final TestObject base = new TestObject();
		final NestableCollectionSafeObject item1 = new NestableCollectionSafeObject("item");
		item1.setValue("foo");
		base.getCollection().add(item1);

		final TestObject modified = new TestObject();
		final NestableCollectionSafeObject item2 = new NestableCollectionSafeObject("item");
		item2.setValue("bar");
		modified.getCollection().add(item2);

		final PropertyPath path = new PropertyPathBuilder()
				.root()
				.propertyName("collection")
				.collectionItem(new NestableCollectionSafeObject("item"))
				.toPropertyPath();
		final DiffNode<NestableCollectionSafeObject> difference = (DiffNode<NestableCollectionSafeObject>) new BeanDiffer()
				.compare(modified, base)
				.getChild(path);
		assertThat(difference.getType(), Is.is(equalTo(DifferenceType.CHANGED)));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testIgnore()
	{
		final TestObject base = new TestObject();
		final TestObject modified = new TestObject();
		final NestableCollectionSafeObject item = new NestableCollectionSafeObject("foo");
		item.setValue("meh");
		modified.getCollection().add(item);
		final PropertyPath ignorePath = new PropertyPathBuilder()
				.root()
				.propertyName("collection")
				.collectionItem(new NestableCollectionSafeObject("foo"))
				.toPropertyPath();
		final BeanDiffer objectDiffer = new BeanDiffer();
		objectDiffer.addIgnoreProperty(ignorePath);
		final DiffNode<?> rootDifference = objectDiffer.compare(modified, base);
		final DiffNode<?> collectionDifference = rootDifference.getChild("collection");
		final DiffNode<?> itemDifference = rootDifference.getChild(ignorePath);
		assertThat(collectionDifference, nullValue());
		assertThat(itemDifference, nullValue());
	}

	@Test
	public void testMapItemAdd()
	{
		final TestObject base = new TestObject();
		final TestObject modified = new TestObject();
		modified.getMap().put("test", new NestableCollectionSafeObject("test"));
		final PropertyPath path = new PropertyPathBuilder()
				.root()
				.propertyName("map")
				.mapKey("test")
				.toPropertyPath();
		final DiffNode<?> difference = new BeanDiffer().compare(modified, base).getChild(path);
		assertThat(difference.getType(), is(DifferenceType.ADDED));
	}

	@Test
	public void testMapItemRemove()
	{
		final TestObject base = new TestObject();
		base.getMap().put("test", new NestableCollectionSafeObject("test"));
		final TestObject modified = new TestObject();
		final PropertyPath path = new PropertyPathBuilder()
				.root()
				.propertyName("map")
				.mapKey("test")
				.toPropertyPath();
		final DiffNode<?> difference = new BeanDiffer().compare(modified, base).getChild(path);
		assertThat(difference.getType(), is(DifferenceType.REMOVED));
	}

	@Test
	public void testMapItemChange()
	{
		final TestObject base = new TestObject();
		base.getMap().put("test", new NestableCollectionSafeObject("test"));
		final TestObject modified = new TestObject();
		final NestableCollectionSafeObject item = new NestableCollectionSafeObject("test");
		item.setValue("foo");
		modified.getMap().put("test", item);
		final PropertyPath path = new PropertyPathBuilder()
				.root()
				.propertyName("map")
				.mapKey("test")
				.toPropertyPath();
		final DiffNode<TestObject> compare = new BeanDiffer().compare(modified, base);
		final DiffNode<?> difference = compare.getChild(path);
		assertThat(difference.getType(), is(DifferenceType.CHANGED));
	}

	@Test
	public void testNullBase()
	{
		final DiffNode<TestObject> difference = new BeanDiffer().compare(new TestObject(), null);
		assertThat(difference.getType(), is(DifferenceType.ADDED));
	}

	@Test
	public void testEqualsOnlyAnnotation()
	{
		final EqualsOnlyItemContainer itemContainer1 = new EqualsOnlyItemContainer();
		final EqualsOnlyItemContainer itemContainer2 = new EqualsOnlyItemContainer();
		final EqualsOnlyItem item1 = new EqualsOnlyItem("foo");
		itemContainer1.setItem(item1);
		final EqualsOnlyItem item2 = new EqualsOnlyItem("bar");
		itemContainer2.setItem(item2);
		final ObjectDiffer objectDiffer = new BeanDiffer();
		final DiffNode<EqualsOnlyItemContainer> difference = objectDiffer.compare(itemContainer1, itemContainer2);
		final DiffNode<?> child = difference.getChild(PropertyPathBuilder.pathOf("item"));

		// set new item
		child.getCanonicalAccessor().set(itemContainer1, item2);

		assertFalse("Node shouldn't have children", child.hasChildren());
		assertThat("Node should be considered changed", child.getType(), Is.is(equalTo(DifferenceType.CHANGED)));
		assertThat("Item should have been replaced", itemContainer1.getItem(), equalTo(item2));
	}

	@Test
	public void testWithTwoNestingLevels()
	{

	}

	@Test
	public void testWithThreeNestingLevels()
	{
		final NestableCollectionSafeObject
				modified = new NestableCollectionSafeObject("1").setItem(new NestableCollectionSafeObject("2").setItem(new NestableCollectionSafeObject("3").setItem(new NestableCollectionSafeObject("4"))));
		final NestableCollectionSafeObject
				base = new NestableCollectionSafeObject("1").setItem(new NestableCollectionSafeObject("2").setItem(new NestableCollectionSafeObject("3")));

		final ObjectDiffer objectDiffer = new BeanDiffer();
		final DiffNode<NestableCollectionSafeObject> node = objectDiffer.compare(modified, base);
		node.visit(new PrintingVisitor(base, modified));
	}

//	@Test
//	public void testExperiment()
//	{
//		final TestObject base = new TestObject();
//		final TestObject modified = new TestObject();
//		modified.getExperiment().put("test", Arrays.asList(new Item("foo")));
//		final IDifference<TestObject> difference = new ObjectDiffer().compare(modified, base);
//	}
}
