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

package de.danielbechler.diff.visitor;

import de.danielbechler.diff.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import org.testng.annotations.*;

import static org.fest.assertions.api.Assertions.*;

/** @author Daniel Bechler */
public class PrintingVisitorTest
{
	@Test
	public void prints_root_node_if_unchanged_and_without_children()
	{
		final Node rootNode = new DefaultNode(String.class);
		final TestablePrintingVisitor visitor = new TestablePrintingVisitor("foo", "foo");
		rootNode.visit(visitor);
		final String output = visitor.getOutput();
		assertThat(output).isEqualTo("Property at path '/' has not changed\n");
	}

	@Test
	public void omits_intermediate_nodes_with_changed_child_nodes()
	{
		final ObjectWithCircularReference a1 = new ObjectWithCircularReference("a");
		final ObjectWithCircularReference b1 = new ObjectWithCircularReference("b");
		final ObjectWithCircularReference c1 = new ObjectWithCircularReference("c");
		a1.setReference(b1);
		b1.setReference(c1);

		final ObjectWithCircularReference a2 = new ObjectWithCircularReference("a");
		final ObjectWithCircularReference b2 = new ObjectWithCircularReference("b");
		final ObjectWithCircularReference d2 = new ObjectWithCircularReference("d");
		a2.setReference(b2);
		b2.setReference(d2);

		final Node rootNode = ObjectDifferFactory.getInstance().compare(a1, a2);
		final TestablePrintingVisitor visitor = new TestablePrintingVisitor(a1, a2);
		rootNode.visit(visitor);
		final String output = visitor.getOutput();
		assertThat(output).isEqualTo("Property at path '/reference/reference/id' has changed from [ d ] to [ c ]\n");
	}

	private static class TestablePrintingVisitor extends PrintingVisitor
	{
		private final StringBuilder sb = new StringBuilder();

		public TestablePrintingVisitor(final Object working, final Object base)
		{
			super(working, base);
		}

		@Override
		protected void print(final String text)
		{
			sb.append(text).append('\n');
		}

		public String getOutput()
		{
			return sb.toString();
		}
	}
}
