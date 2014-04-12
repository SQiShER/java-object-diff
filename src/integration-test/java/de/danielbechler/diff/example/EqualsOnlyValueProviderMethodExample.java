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
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.PrintingVisitor;
import de.danielbechler.diff.path.NodePath;

class EqualsOnlyValueProviderMethodExample
{
	private EqualsOnlyValueProviderMethodExample()
	{
	}

	public static void main(final String[] args)
	{
		final PropertyClass prop = new PropertyClass("1", "2");
		final EncompassingClass base = new EncompassingClass(prop);
		final PropertyClass prop2 = new PropertyClass("1", "3");
		final EncompassingClass working = new EncompassingClass(prop2);

		final ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();

		// (Option 1) Causes the ObjectDiffer to compare using the method "getProp1" on the 'prop' property of the root object
		builder.configure().comparison()
				.ofNode(NodePath.with("prop"))
				.toUseEqualsMethodOfValueProvidedByMethod("getProp1");

		final DiffNode node = builder.build().compare(working, base);

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
		//@ObjectDiffProperty(equalsOnlyValueProviderMethod = "getProp1")
		public PropertyClass getProp()
		{
			return prop;
		}
	}

	/* (Option 3) This annotation causes the ObjectDiffer to use getProp1 method to compare */
	//@ObjectDiffEqualsOnlyValueProvidedType(method="getProp1")
	public static class PropertyClass
	{
		private String prop1;
		private String prop2;

		public PropertyClass(final String prop1, final String prop2)
		{
			this.prop1 = prop1;
			this.prop2 = prop2;
		}

		public String getProp1()
		{
			return prop1;
		}

		public String getProp2()
		{
			return prop2;
		}
	}

}
