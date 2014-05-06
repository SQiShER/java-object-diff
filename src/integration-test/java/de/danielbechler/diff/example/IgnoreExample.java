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

package de.danielbechler.diff.example;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.introspection.ObjectDiffProperty;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.PrintingVisitor;
import de.danielbechler.diff.path.NodePath;

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
		builder.inclusion().exclude().node(NodePath.with("password"));

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
