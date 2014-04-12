package de.danielbechler.diff.example;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.config.introspection.ObjectDiffProperty;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.visitors.PrintingVisitor;

/**
 * @author Daniel Bechler
 */
@SuppressWarnings("UnusedDeclaration")
class IgnoreExample
{
	private IgnoreExample()
	{
	}

	public static void main(final String[] args)
	{
		final User base = new User("foo", "1234");
		final User working = new User("foo", "9876");

		final ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();

		// (Option 1) Causes the ObjectDiffer to ignore the 'password' property of the root object
		builder.configure().inclusion().exclude().node(NodePath.with("password"));

		final DiffNode node = builder.build().compare(working, base);

		node.visit(new PrintingVisitor(working, base));

		// Output with ignore: Property at path '/' has not changed
		// Output without ignore: Property at path '/password' has changed from [ 1234 ] to [ 9876 ]
	}

	public static class User
	{
		private final String name;
		private final String password;

		public User(final String name, final String password)
		{
			this.name = name;
			this.password = password;
		}

		public String getName()
		{
			return name;
		}

		/* (Option 2) This annotation causes the ObjectDiffer to always ignore this property */
		@ObjectDiffProperty(excluded = true)
		public String getPassword()
		{
			return password;
		}
	}
}
