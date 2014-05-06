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

package de.danielbechler.diff.category;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.path.NodePath;

/**
 * Allows to assign custom categories (or tags) to entire types or selected elements and properties. These categories
 * come in very handy, when combined with the `InclusionConfiguration`. They make it very easy to limit the comparison
 * to a specific subset of the object graph.
 *
 * @author Daniel Bechler
 */
public interface CategoryConfigurer
{
	Of ofNode(NodePath nodePath);

	Of ofType(Class<?> type);

	ObjectDifferBuilder and();

	public interface Of
	{
		CategoryConfigurer toBe(String... categories);
	}
}
