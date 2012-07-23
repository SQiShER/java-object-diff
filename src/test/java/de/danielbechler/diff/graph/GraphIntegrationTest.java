package de.danielbechler.diff.graph;

import de.danielbechler.diff.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import org.junit.*;

/** @author https://github.com/oplohmann */
public class GraphIntegrationTest
{
	@Test
	public void basicNode()
	{
		// works correctly
		final GraphNode base = new GraphNode();
		final GraphNode a = new GraphNode("a");
		base.setDirectReference(a);

		final GraphNode modified = new GraphNode();
		final GraphNode modifiedA = new GraphNode("ax");
		modified.setDirectReference(modifiedA);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
		 * expected output:
		 *
		 * Property at path '.directReference.value' has changed from [ a ] to [ ax ]
		 */
	}

	@Test
	public void basicNodeWithDirectReferences()
	{
		// works correctly
		final GraphNode base = new GraphNode();
		final GraphNode a = new GraphNode("a");
		base.setDirectReference(a);

		final GraphNode modified = new GraphNode();
		final GraphNode modifiedA = new GraphNode("ax");
		modified.setDirectReference(modifiedA);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
		 * expected output:
		 *
		 * Property at path '.directReference.value' has changed from [ a ] to [ ax ]
		 */
	}

	@Test
	public void basicBidirectionalWithChildren()
	{
		// works correctly

		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		base.getChildren().add(a);
		final GraphNode b = new GraphNode(3, "b");
		base.getChildren().add(b);
		a.setDirectReference(b);
		b.setDirectReference(a);

		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "ax");
		modified.getChildren().add(modifiedA);
		final GraphNode modifiedB = new GraphNode(3, "by");
		modified.getChildren().add(modifiedB);
		modifiedA.setDirectReference(modifiedB);
		modifiedB.setDirectReference(modifiedA);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
		 * expected output:
		 *
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@21].directReference.directReference.value' has changed from [ a ] to [ ax ]
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@21].directReference.value' has changed from [ b ] to [ by ]
		 */
	}

	@Test
	public void basicBidirectionalWithChildrenAndMaps()
	{
		// works correctly

		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		base.getMap().put("a", "a");
		final GraphNode b = new GraphNode(3, "b");
		base.getChildren().add(b);
		a.setDirectReference(b);
		b.setDirectReference(a);
		a.getMap().put("node-b", b);
		a.getMap().put("node-x", b);
		b.getMap().put("node-a", a);

		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "ax");
		modified.getMap().put("a", "az");
		final GraphNode modifiedB = new GraphNode(3, "by");
		modified.getChildren().add(modifiedB);
		modifiedA.setDirectReference(modifiedB);
		modifiedB.setDirectReference(modifiedA);
		modifiedA.getMap().put("node-b", modifiedB);
		modifiedB.getMap().put("node-a", modifiedA);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
		 * expected output:
		 *
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].directReference.directReference.map.{node-a}.map.{node-x}' with value [ com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3] ] has been removed
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].directReference.directReference.map.{node-a}.map.{node-b}.value' has changed from [ b ] to [ by ]
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].directReference.directReference.map.{node-a}.value' has changed from [ a ] to [ ax ]
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].directReference.directReference.value' has changed from [ b ] to [ by ]
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].directReference.map.{node-x}' with value [ com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3] ] has been removed
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].directReference.value' has changed from [ a ] to [ ax ]
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].value' has changed from [ b ] to [ by ]
		 * Property at path '.map.{a}' has changed from [ a ] to [ az ]
		 */
	}

	@Test
	public void basicBidirectionalWithoutChildren()
	{
		// works correctly

		final GraphNode a = new GraphNode(1, "a");
		final GraphNode b = new GraphNode(2, "b");
		a.setDirectReference(b);
		b.setDirectReference(a);

		final GraphNode modifiedA = new GraphNode(1, "ax");
		final GraphNode modifiedB = new GraphNode(2, "by");
		modifiedA.setDirectReference(modifiedB);
		modifiedB.setDirectReference(modifiedA);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modifiedA, a);
		root.visit(new PrintingVisitor(modifiedA, a));

		/*
		 * expected output:
		 *
		 * Property at path '.directReference.directReference.value' has changed from [ a ] to [ ax ]
		 * Property at path '.directReference.value' has changed from [ b ] to [ by ]
		 */
	}

	@Test
	public void basicNodeWithDirectReferences2()
	{
		// works correctly
		final GraphNode base = new GraphNode("base");
		final GraphNode a = new GraphNode("a");
		base.setDirectReference(a);

		final GraphNode modified = new GraphNode("modified");
		final GraphNode modifiedA = new GraphNode("ax");
		modified.setDirectReference(modifiedA);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
				 * expected output:
				 *
				 * Property at path '.directReference.value' has changed from [ a ] to [ ax ]
				 * Property at path '.value' has changed from [ base ] to [ modified ]
				 */
	}

	@Test
	public void basicBidirectionalNodeWithChildNodes()
	{
		// does not detect any changes since no primary key defined for each node
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

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));
	}

	@Test
	public void basicBidirectionalNodeWithChildNodesWithIds()
	{
		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		final GraphNode b = new GraphNode(3, "b");
		base.getChildren().add(a);
		base.getChildren().add(b);

		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "ax");
		final GraphNode modifiedB = new GraphNode(3, "by");
		modified.getChildren().add(modifiedA);
		modified.getChildren().add(modifiedB);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
		 * expected output:
		 *
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@21].value' has changed from [ a ] to [ ax ]
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22].value' has changed from [ b ] to [ by ]
		 */
	}

	@Test
	public void simpleGraph()
	{
		// works correctly

		GraphNode base = new GraphNode(1);
		GraphNode a = new GraphNode(2, base, "a");
		base.addChild(a);

		GraphNode modified = new GraphNode(1);
		GraphNode modifiedA = new GraphNode(2, modified, "a");

		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
		 * expected output: nothing changed
		 */

		base = new GraphNode(1);
		a = new GraphNode(2, base, "a");

		modified = new GraphNode(1);
		modifiedA = new GraphNode(2, modified, "ax");

		objectDiffer = ObjectDifferFactory.getInstance();
		root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
		 * expected output:
		 *
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@21].parent.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@21].value' has changed from [ a ] to [ ax ]
		 */
	}

	@Test
	public void simpleGraphExtended()
	{
		// works correctly

		final GraphNode base = new GraphNode(1);
		final GraphNode a = new GraphNode(2, "a");
		establishParentChildRelationship(base, a);
		final GraphNode b = new GraphNode(3, "b");
		establishParentChildRelationship(base, b);

		final GraphNode modified = new GraphNode(1);
		final GraphNode modifiedA = new GraphNode(2, "a");
		establishParentChildRelationship(modified, modifiedA);
		final GraphNode modifiedB = new GraphNode(3, "bx");
		establishParentChildRelationship(modified, modifiedB);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base)
		{
//			@Override
//			protected boolean filter(final Node node)
//			{
//				return node.isCircular();
//			}
		});

//		NodeAssert.assertThat(root)
//				  .hasChild(new PropertyPathBuilder().withRoot()
//													 .withPropertyName("children")
//													 .withCollectionItem(modifiedA)
//													 .withPropertyName("parent")
//													 .build())
//				  .withState(Node.State.CIRCULAR);
//
//		NodeAssert.assertThat(root)
//				  .hasChild(new PropertyPathBuilder().withRoot()
//													 .withPropertyName("children")
//													 .withCollectionItem(modifiedB)
//													 .withPropertyName("parent")
//													 .build())
//				  .withState(Node.State.CIRCULAR);

		NodeAssert.assertThat(root)
				  .hasChild(new PropertyPathBuilder().withRoot()
													 .withPropertyName("children")
													 .withCollectionItem(modifiedB)
													 .withPropertyName("value")
													 .build())
				  .withState(Node.State.CHANGED);
		/*
				 * expected output:
				 *
				 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@21[id=2]].parent.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].value' has changed from [ b ] to [ bx ]
				 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].value' has changed from [ b ] to [ bx ]
				 */
	}

	private static void establishParentChildRelationship(final GraphNode parent, final GraphNode child)
	{
		child.setParent(parent);
		parent.addChild(child);
	}

	@Test
	public void bidirectionalGraphStackOverflow()
	{
		// works correctly

		final GraphNode base = new GraphNode(1);

		final GraphNode a = new GraphNode(2, base, "a");
		final GraphNode b = new GraphNode(3, base, "b");
		a.setDirectReference(b);
		b.setDirectReference(a);
		base.getChildren().add(a);
		base.getChildren().add(b);

		final GraphNode aa = new GraphNode(4, a, "aa");
		a.getChildren().add(aa);

		final GraphNode ba = new GraphNode(5, b, "ba");
		b.getChildren().add(ba);

		aa.getChildren().add(ba);
		ba.getChildren().add(aa);

		final GraphNode baa = new GraphNode(6, ba, "baa");
		ba.getChildren().add(baa);

		final GraphNode modified = new GraphNode(1);

		final GraphNode modifiedA = new GraphNode(2, modified, "a");
		final GraphNode modifiedB = new GraphNode(3, modified, "b");
		modifiedA.setDirectReference(modifiedB);
		modifiedB.setDirectReference(modifiedA);
		modified.getChildren().add(modifiedA);
		modified.getChildren().add(modifiedA);

		final GraphNode modifiedAA = new GraphNode(4, modifiedA, "aa");
		modifiedA.getChildren().add(modifiedAA);

		final GraphNode modifiedBA = new GraphNode(5, modifiedB, "ba-x");
		modifiedB.getChildren().add(modifiedBA);

		modifiedAA.getChildren().add(modifiedBA);
		modifiedBA.getChildren().add(modifiedAA);

		final GraphNode modifieBAA = new GraphNode(6, modifiedBA, "baa-y");
		modifiedBA.getChildren().add(modifieBAA);

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();

		final Node root = objectDiffer.compare(modified, base);
		root.visit(new PrintingVisitor(modified, base));

		/*
		 * expected output:
		 *
		 * Property at path '[com.bisnode.platform.javaobjectdiff.model.MyNode@25[id=6]].parent.value' has changed from [ ba ] to [ ba-x ]
		 * Property at path '[com.bisnode.platform.javaobjectdiff.model.MyNode@25[id=6]].value' has changed from [ baa ] to [ baa-y ]
		 * Property at path '[com.bisnode.platform.javaobjectdiff.model.MyNode@24[id=5]].value' has changed from [ ba ] to [ ba-x ]
		 */
	}

}
