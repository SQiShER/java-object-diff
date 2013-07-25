package de.danielbechler.diff.example;

import java.util.ArrayList;
import java.util.List;

import de.danielbechler.diff.Configuration;
import de.danielbechler.diff.ObjectDifferFactory;
import de.danielbechler.diff.annotation.ObjectDiffMethodEqualsType;
import de.danielbechler.diff.annotation.ObjectDiffProperty;
import de.danielbechler.diff.node.Node;
import de.danielbechler.diff.path.PropertyPath;
import de.danielbechler.diff.visitor.PrintingVisitor;

class MethodEqualExample {
	private MethodEqualExample()
	{
	}

	public static void main(final String[] args)
	{
		PropertyClass prop = new PropertyClass("1", "2");
		final EncompassingClass base = new EncompassingClass(prop);
		PropertyClass prop2 = new PropertyClass("1", "3");
		final EncompassingClass working = new EncompassingClass(prop2);

		final Configuration configuration = new Configuration();

		// (Option 1) Causes the ObjectDiffer to compare using the method "getProp1" on the 'prop' property of the root object
		configuration.withMethodEqualsProperty(PropertyPath.buildWith("prop"), "getProp1");

		final Node node = ObjectDifferFactory.getInstance(configuration).compare(working, base);

		node.visit(new PrintingVisitor(working, base));

		// Output with ignore: 
		//	Property at path '/' has not changed
		// Output without ignore: 
		// Property at path '/prop/prop2' has changed from [ 2 ] to [ 3 ]
	}

	public static class EncompassingClass
	{
		private final PropertyClass prop;

		public EncompassingClass(final PropertyClass prop)
		{
			this.prop = prop;
		}

		/* (Option 2) This annotation causes the ObjectDiffer to use getProp1 method to compare */
		//@ObjectDiffProperty(methodEqual = "getProp1")
		public PropertyClass getProp() {
			return prop;
		}
	}
	
	/* (Option 3) This annotation causes the ObjectDiffer to use getProp1 method to compare */
	//@ObjectDiffMethodEqualsType(method="getProp1")
	public static class PropertyClass
	{
		private String prop1;
		private String prop2;
		
		public PropertyClass(String prop1, String prop2)
		{
			this.prop1 = prop1;
			this.prop2 = prop2;
		}
		public String getProp1() {
			return prop1;
		}
		public String getProp2() {
			return prop2;
		}
	}

}
