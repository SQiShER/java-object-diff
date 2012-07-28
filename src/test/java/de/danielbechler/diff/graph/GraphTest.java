package de.danielbechler.diff.graph;

import org.junit.Test;



import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferFactory;
import de.danielbechler.diff.node.Node;
import de.danielbechler.diff.visitor.PrintingVisitor;

public class GraphTest 
{

	@Test
	public void basicNode() 
	{	
		// works correctly
		MyNode base = new MyNode();		
		MyNode a = new MyNode("a");		
		base.setDirectReference(a);	

		MyNode modified = new MyNode();
		MyNode modifiedA = new MyNode("ax");		
		modified.setDirectReference(modifiedA);				
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
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
		MyNode base = new MyNode();		
		MyNode a = new MyNode("a");
		base.setDirectReference(a);

		MyNode modified = new MyNode();
		MyNode modifiedA = new MyNode("ax");		
		modified.setDirectReference(modifiedA);		
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
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
		
		MyNode base = new MyNode(1);		
		MyNode a = new MyNode(2, "a");
		base.getChildren().add(a);
		MyNode b = new MyNode(3, "b");
		base.getChildren().add(b);
		a.setDirectReference(b);
		b.setDirectReference(a);

		MyNode modified = new MyNode(1);
		MyNode modifiedA = new MyNode(2, "ax");
		modified.getChildren().add(modifiedA);
		MyNode modifiedB = new MyNode(3, "by");
		modified.getChildren().add(modifiedB);
		modifiedA.setDirectReference(modifiedB);
		modifiedB.setDirectReference(modifiedA);
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
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
		
		MyNode base = new MyNode(1);		
		MyNode a = new MyNode(2, "a");
		base.getMap().put("a", "a");
		MyNode b = new MyNode(3, "b");
		base.getChildren().add(b);
		a.setDirectReference(b);
		b.setDirectReference(a);
		a.getMap().put("node-b", b);
		a.getMap().put("node-x", b);
		b.getMap().put("node-a", a);

		MyNode modified = new MyNode(1);
		MyNode modifiedA = new MyNode(2, "ax");
		modified.getMap().put("a", "az");
		MyNode modifiedB = new MyNode(3, "by");
		modified.getChildren().add(modifiedB);
		modifiedA.setDirectReference(modifiedB);
		modifiedB.setDirectReference(modifiedA);
		modifiedA.getMap().put("node-b", modifiedB);
		modifiedB.getMap().put("node-a", modifiedA);
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
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
		
		MyNode a = new MyNode(1, "a");
		MyNode b = new MyNode(2, "b");
		a.setDirectReference(b);
		b.setDirectReference(a);

		MyNode modifiedA = new MyNode(1, "ax");
		MyNode modifiedB = new MyNode(2, "by");
		modifiedA.setDirectReference(modifiedB);
		modifiedB.setDirectReference(modifiedA);
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modifiedA, a);		
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
		MyNode base = new MyNode("base");		
		MyNode a = new MyNode("a");
		base.setDirectReference(a);

		MyNode modified = new MyNode("modified");
		MyNode modifiedA = new MyNode("ax");		
		modified.setDirectReference(modifiedA);		
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
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
		MyNode base = new MyNode();		
		MyNode a = new MyNode("a");
		MyNode b = new MyNode("b");	
		base.getChildren().add(a);
		base.getChildren().add(b);

		MyNode modified = new MyNode();
		MyNode modifiedA = new MyNode("a");		
		MyNode modifiedB = new MyNode("bx");
		modified.getChildren().add(modifiedA);
		modified.getChildren().add(modifiedB);
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
		root.visit(new PrintingVisitor(modified, base));
	}
	
	@Test
	public void basicBidirectionalNodeWithChildNodesWithIds() 
	{	
		MyNode base = new MyNode(1);		
		MyNode a = new MyNode(2, "a");
		MyNode b = new MyNode(3, "b");	
		base.getChildren().add(a);
		base.getChildren().add(b);

		MyNode modified = new MyNode(1);
		MyNode modifiedA = new MyNode(2, "ax");		
		MyNode modifiedB = new MyNode(3, "by");
		modified.getChildren().add(modifiedA);
		modified.getChildren().add(modifiedB);
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
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
		
		MyNode base = new MyNode(1);		
		MyNode a = new MyNode(2, base, "a");

		MyNode modified = new MyNode(1);		
		MyNode modifiedA = new MyNode(2, modified, "a");
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
		root.visit(new PrintingVisitor(modified, base));
		
		/*
		 * expected output: nothing changed
		 */
		
		base = new MyNode(1);		
		a = new MyNode(2, base, "a");

		modified = new MyNode(1);		
		modifiedA = new MyNode(2, modified, "ax");
				
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
		
		MyNode base = new MyNode(1);
		
		MyNode a = new MyNode(2, base, "a");
		MyNode b = new MyNode(3, base, "b");

		MyNode modified = new MyNode(1);
		
		MyNode modifiedA = new MyNode(2, modified, "a");
		MyNode modifiedB = new MyNode(3, modified, "bx");
				
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Node root = objectDiffer.compare(modified, base);		
		root.visit(new PrintingVisitor(modified, base));
		
		/*
		 * expected output:
		 * 
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@21[id=2]].parent.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].value' has changed from [ b ] to [ bx ]
		 * Property at path '.children.[com.bisnode.platform.javaobjectdiff.model.MyNode@22[id=3]].value' has changed from [ b ] to [ bx ]
		 */
	}
	

	
	@Test
	public void bidirectionalGraphStackOverflow() 
	{
		// works correctly
		
		MyNode base = new MyNode(1);
		
		MyNode a = new MyNode(2, base, "a");
		MyNode b = new MyNode(3, base, "b");
		a.setDirectReference(b);
		b.setDirectReference(a);
		base.getChildren().add(a);
		base.getChildren().add(b);
				
		MyNode aa = new MyNode(4, a, "aa");
		a.getChildren().add(aa);
		
		MyNode ba = new MyNode(5, b, "ba");
		b.getChildren().add(ba);
		
		aa.getChildren().add(ba);
		ba.getChildren().add(aa);
		
		MyNode baa = new MyNode(6, ba, "baa");
		ba.getChildren().add(baa);

		
		MyNode modified = new MyNode(1);
		
		MyNode modifiedA = new MyNode(2, modified, "a");
		MyNode modifiedB = new MyNode(3, modified, "b");
		modifiedA.setDirectReference(modifiedB);
		modifiedB.setDirectReference(modifiedA);
		modified.getChildren().add(modifiedA);
		modified.getChildren().add(modifiedA);
				
		MyNode modifiedAA = new MyNode(4, modifiedA, "aa");
		modifiedA.getChildren().add(modifiedAA);
		
		MyNode modifiedBA = new MyNode(5, modifiedB, "ba-x");
		modifiedB.getChildren().add(modifiedBA);
		
		modifiedAA.getChildren().add(modifiedBA);
		modifiedBA.getChildren().add(modifiedAA);
		
		MyNode modifieBAA = new MyNode(6, modifiedBA, "baa-y");
		modifiedBA.getChildren().add(modifieBAA);
		
		ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();

		Node root = objectDiffer.compare(modified, base);
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
