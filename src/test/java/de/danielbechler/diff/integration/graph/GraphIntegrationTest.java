package de.danielbechler.diff.integration.graph;

import de.danielbechler.diff.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.visitor.*;
import org.junit.*;

import static de.danielbechler.diff.node.Node.State.*;
import static de.danielbechler.diff.node.NodeAssertions.assertThat;
import static de.danielbechler.diff.path.PropertyPath.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * This tests have been provided by a user who had some trouble with circular relationships. The introduction
 * of the {@link CircularReferenceDetector} fixed his problems. To avoid regression, these integration tests
 * will be kept until other tests cover the same scenarios.
 *
 * @author https://github.com/oplohmann (original author)
 * @author Daniel Bechler (modifications)
 */
public class GraphIntegrationTest
{
	private static final boolean PRINT_ENABLED = false;

	@Test
	public void basicNode()
	{
		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		base.setDirectReference(a);

		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "ax");
		modified.setDirectReference(modifiedA);

		final Node root = compareAndPrint(modified, base);

		assertThat(root).node().hasChildren(1);
		assertThat(root).child("directReference", "value").hasState(Node.State.CHANGED).hasNoChildren();
	}

	@Test
	public void basicNodeWithDirectReferences()
	{
		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		base.setDirectReference(a);

		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "ax");
		modified.setDirectReference(modifiedA);

		final Node root = compareAndPrint(modified, base);

		assertThat(root).node().hasChildren(1);
		assertThat(root).child("directReference", "value").hasState(Node.State.CHANGED).hasNoChildren();
	}

	@Test
	public void basicBidirectionalWithChildren()
	{
		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		final GraphNode b = new GraphNode(3, "b");
		base.addChildren(a, b);
		establishCircularDirectReference(a, b);

		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "ax");
		final GraphNode modifiedB = new GraphNode(3, "by");
		modified.addChildren(modifiedA, modifiedB);
		establishCircularDirectReference(modifiedA, modifiedB);

		final Node root = compareAndPrint(modified, base);

		assertThat(root).node().hasChildren(1);
		assertThat(root).child("children").hasChildren(2);
		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(a))
				.hasState(Node.State.CHANGED);
		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(b))
				.hasState(Node.State.CHANGED);
	}

	@Test //(timeout = 1000)
	public void basicBidirectionalWithChildrenAndMaps()
	{
		final GraphNode a = new GraphNode(2, "a");
		final GraphNode b = new GraphNode(3, "b");
		establishCircularDirectReference(a, b);
		a.getMap().put("node-b", b);
		a.getMap().put("node-b-again", b);
		b.getMap().put("node-a", a);

		final GraphNode base = new GraphNode(1);
		base.getMap().put("a", "a");
		base.getChildren().add(b);

		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "ax");
		modified.getMap().put("a", "az");
		final GraphNode modifiedB = new GraphNode(3, "by");
		modified.addChild(modifiedB);
		establishCircularDirectReference(modifiedA, modifiedB);
		modifiedA.getMap().put("node-b", modifiedB);
		modifiedB.getMap().put("node-a", modifiedA);

		final Node root = compareAndPrint(modified, base);

		/*
		 * one could assume that this node would be marked as removed, but since the instance it represents
		 * is a parent of it, we cannot reliably test, whether the node has been changed. (added or removed
		 * is easier, but since changed is not yet possible, that would be inconsistent)
		 */
		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(b)
				.withPropertyName("directReference")
				.withPropertyName("map")
				.withMapKey("node-b-again"))
				.hasState(Node.State.CIRCULAR); // TODO Change circular reference detection, to allow returning the expected "REMOVED" state

		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(b)
				.withPropertyName("directReference")
				.withPropertyName("value")).hasState(Node.State.CHANGED);

		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(b)
				.withPropertyName("map")
				.withMapKey("node-a")
				.withPropertyName("map")
				.withMapKey("node-b-again"))
				.hasState(Node.State.CIRCULAR); // TODO Change circular reference detection, to allow returning the expected "REMOVED" state

		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(b)
				.withPropertyName("map")
				.withMapKey("node-a")
				.withPropertyName("value")).hasState(Node.State.CHANGED);

		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(b)
				.withPropertyName("value")).hasState(Node.State.CHANGED);

		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("map")
				.withMapKey("a")).hasState(Node.State.CHANGED);
	}

	@Test
	public void basicBidirectionalWithoutChildren()
	{
		final GraphNode a = new GraphNode(1, "a");
		final GraphNode b = new GraphNode(2, "b");
		establishCircularDirectReference(a, b);

		final GraphNode modifiedA = new GraphNode(1, "ax");
		final GraphNode modifiedB = new GraphNode(2, "by");
		establishCircularDirectReference(modifiedA, modifiedB);

		final Node root = compareAndPrint(modifiedA, a);

		assertThat(root).child("directReference", "value").hasState(Node.State.CHANGED).hasNoChildren();
		assertThat(root).child("value").hasState(Node.State.CHANGED).hasNoChildren();
	}

	@Test
	public void basicNodeWithDirectReferences2()
	{
		final GraphNode base = new GraphNode("base");
		final GraphNode a = new GraphNode("a");
		base.setDirectReference(a);

		final GraphNode modified = new GraphNode("modified");
		final GraphNode modifiedA = new GraphNode("ax");
		modified.setDirectReference(modifiedA);

		final Node root = compareAndPrint(modified, base);

		assertThat(root).child("directReference", "value").hasState(Node.State.CHANGED).hasNoChildren();
		assertThat(root).child("value").hasState(Node.State.CHANGED).hasNoChildren();
	}

	/** Does not detect any changes since no primary key defined for each node */
	@Test
	public void basicBidirectionalNodeWithChildNodes()
	{
		final GraphNode base = new GraphNode();
		final GraphNode a = new GraphNode("a");
		final GraphNode b = new GraphNode("b");
		base.getChildren().add(a);
		base.getChildren().add(b);

		final GraphNode modified = new GraphNode();
		final GraphNode modifiedA = new GraphNode("a");
		final GraphNode modifiedB = new GraphNode("bx");
		modified.getChildren().add(modifiedA);
		modified.getChildren().add(modifiedB);

		final Node root = compareAndPrint(modified, base);

		NodeAssertions.assertThat(root).node().hasState(Node.State.UNTOUCHED); // not a bug!

		// NOTE: This is expected, since Collections (and java-object-diff) rely heavily on the proper
		// implementation of hashCode and equals. The GraphNode uses the ID as sole criteria in it's
		// equality check. Since all instances created without an explicit ID, will default to -1, all
		// GraphNodes in this example are technically equal.
		//
		// The following test proves this:

		final GraphNode actualA = base.getChildren().get(0);
		final GraphNode actualB = base.getChildren().get(1);
		assertThat(actualA).isEqualTo(a);
		assertThat(actualB).isEqualTo(b);
		assertThat(actualA).isEqualTo(actualB);
	}

	@Test
	public void basicBidirectionalNodeWithChildNodesWithIds()
	{
		final GraphNode a = new GraphNode(2, "a");
		final GraphNode b = new GraphNode(3, "b");
		final GraphNode base = new GraphNode(1);
		base.addChild(a);
		base.addChild(b);

		final GraphNode modifiedA = new GraphNode(2, "ax");
		final GraphNode modifiedB = new GraphNode(3, "by");
		final GraphNode modified = new GraphNode(1);
		modified.addChild(modifiedA);
		modified.addChild(modifiedB);

		final Node root = compareAndPrint(modified, base);

		NodeAssertions.assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(a))
					  .hasState(Node.State.CHANGED)
					  .hasChildren(1);

		NodeAssertions.assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(b))
					  .hasState(Node.State.CHANGED)
					  .hasChildren(1);
	}

	@Test
	public void simpleGraphWithChanges()
	{
		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		establishParentChildRelationship(base, a);
		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "ax");
		establishParentChildRelationship(modified, modifiedA);

		final Node root = compareAndPrint(modified, base);

		NodeAssertions.assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(modifiedA)
				.withPropertyName("value"))
					  .hasState(Node.State.CHANGED);
	}

	@Test
	public void simpleGraphWithoutChanges()
	{
		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		establishParentChildRelationship(base, a);
		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "a");
		establishParentChildRelationship(modified, modifiedA);

		final Node root = compareAndPrint(modified, base);

		assertThat(root).node().hasState(Node.State.UNTOUCHED);
	}

	@Test
	public void simpleGraphExtended()
	{
		final GraphNode a = new GraphNode(2, "a");
		final GraphNode b = new GraphNode(3, "b");
		final GraphNode base = new GraphNode(1);
		establishParentChildRelationship(base, a);
		establishParentChildRelationship(base, b);

		final GraphNode modifiedA = new GraphNode(2, "a");
		final GraphNode modifiedB = new GraphNode(3, "bx");
		final GraphNode modified = new GraphNode(1);
		establishParentChildRelationship(modified, modifiedA);
		establishParentChildRelationship(modified, modifiedB);

		final Node root = compareAndPrint(modified, base);

		assertThat(root).child(createBuilder()
				.withRoot()
				.withPropertyName("children")
				.withCollectionItem(b)
				.withPropertyName("value"))
				.hasState(CHANGED);
	}

	@Test
	public void bidirectionalGraphStackOverflow()
	{
		final GraphNode a = new GraphNode(2, "a");
		final GraphNode b = new GraphNode(3, "b");
		establishCircularDirectReference(a, b);

		final GraphNode base = new GraphNode(1);
		establishParentChildRelationship(base, a);
		establishParentChildRelationship(base, b);

		final GraphNode aa = new GraphNode(4, "aa");
		final GraphNode ba = new GraphNode(5, "ba");
		establishCircularChildRelationship(aa, ba);

		establishParentChildRelationship(a, aa);
		establishParentChildRelationship(b, ba);

		final GraphNode baa = new GraphNode(6, "baa");
		establishParentChildRelationship(ba, baa);

		final GraphNode modifiedA = new GraphNode(2, "a");
		final GraphNode modifiedB = new GraphNode(3, "b");
		establishCircularDirectReference(modifiedA, modifiedB);

		final GraphNode modified = new GraphNode(1);
		establishParentChildRelationship(modified, modifiedA);
		establishParentChildRelationship(modified, modifiedB);

		final GraphNode modifiedAA = new GraphNode(4, "aa");
		final GraphNode modifiedBA = new GraphNode(5, "ba-x");
		establishCircularChildRelationship(modifiedAA, modifiedBA);

		establishParentChildRelationship(modifiedA, modifiedAA);
		establishParentChildRelationship(modifiedB, modifiedBA);

		final GraphNode modifieBAA = new GraphNode(6, "baa-y");
		establishParentChildRelationship(modifiedBA, modifieBAA);

		final Node root = compareAndPrint(modified, base);

		NodeAssertions.assertThat(root)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("children")
							  .withCollectionItem(a)
							  .withPropertyName("children")
							  .withCollectionItem(aa)
							  .withPropertyName("children")
							  .withCollectionItem(ba)
							  .withPropertyName("children")
							  .withCollectionItem(baa))
					  .hasState(Node.State.CHANGED);

		NodeAssertions.assertThat(root)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("children")
							  .withCollectionItem(a)
							  .withPropertyName("children")
							  .withCollectionItem(aa)
							  .withPropertyName("children")
							  .withCollectionItem(ba)
							  .withPropertyName("value"))
					  .hasState(Node.State.CHANGED);

		NodeAssertions.assertThat(root)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("children")
							  .withCollectionItem(a)
							  .withPropertyName("directReference")
							  .withPropertyName("children")
							  .withCollectionItem(ba)
							  .withPropertyName("children")
							  .withCollectionItem(baa)
							  .withPropertyName("value"))
					  .hasState(Node.State.CHANGED);

		NodeAssertions.assertThat(root)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("children")
							  .withCollectionItem(a)
							  .withPropertyName("directReference")
							  .withPropertyName("children")
							  .withCollectionItem(ba)
							  .withPropertyName("value"))
					  .hasState(Node.State.CHANGED);

		NodeAssertions.assertThat(root)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("children")
							  .withCollectionItem(b)
							  .withPropertyName("children")
							  .withCollectionItem(ba)
							  .withPropertyName("children")
							  .withCollectionItem(baa)
							  .withPropertyName("value"))
					  .hasState(Node.State.CHANGED);

		NodeAssertions.assertThat(root)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("children")
							  .withCollectionItem(b)
							  .withPropertyName("children")
							  .withCollectionItem(ba)
							  .withPropertyName("value"))
					  .hasState(Node.State.CHANGED);

		NodeAssertions.assertThat(root)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("children")
							  .withCollectionItem(b)
							  .withPropertyName("directReference")
							  .withPropertyName("children")
							  .withCollectionItem(aa)
							  .withPropertyName("children")
							  .withCollectionItem(ba)
							  .withPropertyName("children")
							  .withCollectionItem(baa)
							  .withPropertyName("value"))
					  .hasState(Node.State.CHANGED);

		NodeAssertions.assertThat(root)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("children")
							  .withCollectionItem(b)
							  .withPropertyName("directReference")
							  .withPropertyName("children")
							  .withCollectionItem(aa)
							  .withPropertyName("children")
							  .withCollectionItem(ba)
							  .withPropertyName("value"))
					  .hasState(Node.State.CHANGED);
	}

	private static Node compareAndPrint(final GraphNode modified, final GraphNode base)
	{
		final Node root = ObjectDifferFactory.getInstance().compare(modified, base);
		if (PRINT_ENABLED)
		{
			root.visit(new PrintingVisitor(modified, base));
		}
		return root;
	}

	@Test
	public void testWithSimpleBiDirectionalConnection()
	{
		final GraphNode working1 = new GraphNode(1, "foo");
		final GraphNode working2 = new GraphNode(2, "bar");
		establishCircularDirectReference(working1, working2);

		final GraphNode base1 = new GraphNode(1);
		final GraphNode base2 = new GraphNode(2);
		establishCircularDirectReference(base1, base2);

		final Configuration configuration = new Configuration().withCircularNodes();
		final Node node = ObjectDifferFactory.getInstance(configuration).compare(working1, base1);
		node.visit(new NodeHierarchyVisitor());

		assertThat(node).child("value").hasState(Node.State.ADDED);
		assertThat(node).child("directReference").hasState(Node.State.CHANGED);
		assertThat(node).child("directReference", "value").hasState(Node.State.ADDED);
		assertThat(node).child("directReference", "directReference").isCircular();
	}

	@Test
	public void testWithMapBasedBiDirectionalConnection()
	{
		final GraphNode working1 = new GraphNode(1, "foo");
		final GraphNode working2 = new GraphNode(2, "bar");
		working1.getMap().put("foo", working2);
		working2.getMap().put("bar", working1);

		final GraphNode base1 = new GraphNode(1);
		final GraphNode base2 = new GraphNode(2);
		base1.getMap().put("foo", base2);
		base2.getMap().put("bar", base1);

		final ObjectDiffer differ = ObjectDifferFactory.getInstance();
		differ.getConfiguration().withCircularNodes();
		final Node node = differ.compare(working1, base1);
		node.visit(new NodeHierarchyVisitor());

		NodeAssertions.assertThat(node)
					  .child(createBuilder()
							  .withRoot()
							  .withPropertyName("map")
							  .withMapKey("foo")
							  .withPropertyName("map")
							  .withMapKey("bar"))
					  .isCircular();
	}

	private static void establishParentChildRelationship(final GraphNode parent, final GraphNode child)
	{
		child.setParent(parent);
		parent.addChild(child);
	}

	private static void establishCircularChildRelationship(final GraphNode a, final GraphNode b)
	{
		a.addChild(b);
		b.addChild(a);
	}

	private static void establishCircularDirectReference(final GraphNode a, final GraphNode b)
	{
		a.setDirectReference(b);
		b.setDirectReference(a);
	}
}
