/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.diff.issues.issue127;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import de.danielbechler.diff.path.NodePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class Issue127Main
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Issue127Main.class);

	private Issue127Main()
	{
	}

	public static void main(final String[] args) throws IOException
	{
		final Map<String, Double> map1 = Collections.singletonMap("lng", 123.45);
		final Map<String, Double> map2 = Collections.singletonMap("lat", 67.89);

		final Community c1 = new Community();
		c1.setName("Foo");
		c1.setGeolocation(map1);

		final Community c2 = new Community();
		c2.setName("Bar");
		c2.setGeolocation(map2);

		final ObjectDiffer objectDiffer = ObjectDifferBuilder
				.startBuilding()
				.comparison().ofNode(NodePath.with("geolocation")).toUseEqualsMethod()
				.and()
				.build();
		final DiffNode diff = objectDiffer.compare(c1, c2);

		if (diff.hasChanges())
		{
			diff.visit(new DiffNode.Visitor()
			{
				public void node(final DiffNode node, final Visit visit)
				{
					final Object oldValue = node.canonicalGet(c1);
					final Object newValue = node.canonicalGet(c2);
					LOGGER.warn("{}: {} => {}", node.getPath(), oldValue, newValue);
				}
			});
		}
	}

	private static class Community
	{
		private String name;
		private List<String> children;
		private Map<String, Double> geolocation;

		public String getName()
		{
			return this.name;
		}

		public void setName(final String name)
		{
			this.name = name;
		}

		public List<String> getChildren()
		{
			return this.children;
		}

		public void setChildren(final List<String> children)
		{
			this.children = children;
		}

		public Map<String, Double> getGeolocation()
		{
			return this.geolocation;
		}

		public void setGeolocation(final Map<String, Double> geolocation)
		{
			this.geolocation = geolocation;
		}
	}
}
