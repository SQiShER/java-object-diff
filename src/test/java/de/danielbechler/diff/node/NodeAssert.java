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

package de.danielbechler.diff.node;

import de.danielbechler.diff.path.*;
import org.junit.*;

import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNull.*;

/** @author Daniel Bechler */
public class NodeAssert
{
	public static NodeAssert assertThat(final Node node)
	{
		return new NodeAssert(node);
	}

	private final Node node;

	public NodeAssert(final Node node)
	{
		this.node = node;
	}

	public Expectations hasChild(final PropertyPath propertyPath)
	{
		Assert.assertThat(node, notNullValue());
		final Node child = node.getChild(propertyPath);
		Assert.assertThat("Expected node at path " + propertyPath.toString(), child, notNullValue());
		return new Expectations(child);
	}

	public Expectations hasChild(final String... at)
	{
		return hasChild(PropertyPath.with(at));
	}

	public static class Expectations
	{
		private final Node node;

		public Expectations(final Node node)
		{
			this.node = node;
		}

		public Expectations withState(final Node.State state)
		{
			Assert.assertThat(node.getState(), equalTo(state));
			return this;
		}
	}

}
