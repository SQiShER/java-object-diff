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

package de.danielbechler.diff.example;

import de.danielbechler.diff.DiffNode;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.visitor.NodeHierarchyVisitor;
import de.danielbechler.diff.visitor.PrintingVisitor;

/**
 * @author Daniel Bechler
 */
class SimpleNodeExample
{
	private SimpleNodeExample()
	{
	}

	private static class Person
	{
		private String firstName;
		private String lastName;

		private Person(final String firstName, final String lastName)
		{
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getFirstName()
		{
			return firstName;
		}

		public void setFirstName(final String firstName)
		{
			this.firstName = firstName;
		}

		public String getLastName()
		{
			return lastName;
		}

		public void setLastName(final String lastName)
		{
			this.lastName = lastName;
		}

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			if (firstName != null)
			{
				sb.append(firstName).append(' ');
			}
			if (lastName != null)
			{
				sb.append(lastName);
			}
			return sb.toString();
		}
	}

	public static void main(final String[] args)
	{
		final Person bruceWayne = new Person("Bruce", "Wayne");
		final Person batman = new Person("Batman", null);
		final DiffNode rootNode = ObjectDifferBuilder.buildDefault().compare(batman, bruceWayne);
		rootNode.visit(new NodeHierarchyVisitor(10));
		rootNode.visit(new PrintingVisitor(batman, bruceWayne)
		{
			@Override
			protected boolean filter(final DiffNode node)
			{
				return true;
			}
		});
	}
}
