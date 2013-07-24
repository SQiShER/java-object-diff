package de.danielbechler.diff.example;

import java.util.ArrayList;
import java.util.List;

import de.danielbechler.diff.Configuration;
import de.danielbechler.diff.ObjectDifferFactory;
import de.danielbechler.diff.annotation.ObjectDiffProperty;
import de.danielbechler.diff.example.IgnoreExample.User;
import de.danielbechler.diff.node.Node;
import de.danielbechler.diff.path.PropertyPath;
import de.danielbechler.diff.visitor.PrintingVisitor;

class MethodEqualExample {
	private MethodEqualExample()
	{
	}

	public static void main(final String[] args)
	{
		List<Object> baseItems = new ArrayList<Object>();
		baseItems.add("baseitem");
		final EncompassingClass base = new EncompassingClass(baseItems);
		List<Object> workingItems = new ArrayList<Object>();
		workingItems.add("workingitem");
		final EncompassingClass working = new EncompassingClass(workingItems);

		final Configuration configuration = new Configuration();

		// (Option 1) Causes the ObjectDiffer to use the method "size" on the 'items' property of the root object
		configuration.withMethodEqualsProperty(PropertyPath.buildWith("items"), "size");

		final Node node = ObjectDifferFactory.getInstance(configuration).compare(working, base);

		node.visit(new PrintingVisitor(working, base));

		// Output with ignore: 
		//	Property at path '/' has not changed
		// Output without ignore: 
		//	Property at path '/items[workingitem]' has been added => [ workingitem ]
		//	Property at path '/items[baseitem]' with value [ baseitem ] has been removed
	}

	public static class EncompassingClass
	{
		private final List<Object> items;

		public EncompassingClass(final List<Object> items)
		{
			this.items = items;
		}

		/* (Option 2) This annotation causes the ObjectDiffer to always ignore this property */
		@ObjectDiffProperty(methodEqual = "size")
		public List<Object> getItems()
		{
			return items;
		}
	}

}
