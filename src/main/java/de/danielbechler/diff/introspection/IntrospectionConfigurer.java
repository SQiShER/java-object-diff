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

package de.danielbechler.diff.introspection;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.path.NodePath;

/**
 * Allows to replace the default bean introspector with a custom implementation. The default introspector internally
 * uses the `java.beans.Introspector` which has some limitations. The most important one being that it only operates on
 * getters and setters. In case field introspection is needed a custom introspector must be used. An introspector can
 * be
 * set as global default or on a per-property basis. It is also possible to turn off introspection for specific
 * properties in which case they will simply be compared via `equals` method.
 *
 * @author Daniel Bechler
 */
public interface IntrospectionConfigurer
{
	IntrospectionConfigurer setDefaultIntrospector(Introspector introspector);

	Of ofType(Class<?> type);

	Of ofNode(NodePath path);

	ObjectDifferBuilder and();

	public static interface Of
	{
		IntrospectionConfigurer toUse(Introspector introspector);

		IntrospectionConfigurer toBeEnabled();

		IntrospectionConfigurer toBeDisabled();
	}
}
