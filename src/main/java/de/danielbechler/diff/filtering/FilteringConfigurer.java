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

package de.danielbechler.diff.filtering;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

/**
 * Allows to exclude nodes from being added to the object graph based on criteria that are only known after the diff
 * for
 * the affected node and all its children has been determined.
 * <p>
 * Currently it is only possible to configure returnability based on the state (_added_, _changed_, _untouched_, etc.)
 * of the `DiffNode`. But this is just the beginning. Nothing speaks against adding more powerful options. It would be
 * nice for example to be able to pass some kind of matcher to determine returnability based on dynamic criteria at
 * runtime.
 *
 * @author Daniel Bechler
 */
public interface FilteringConfigurer
{
	FilteringConfigurer returnNodesWithState(DiffNode.State state, boolean enabled);

	FilteringConfigurer returnNodesWithState(DiffNode.State state);

	FilteringConfigurer omitNodesWithState(DiffNode.State state);

	ObjectDifferBuilder and();
}
