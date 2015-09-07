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

package de.danielbechler.diff.comparison;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.path.NodePath;

/**
 * Allows to configure the way objects are compared. Sometimes introspection is just not the way to go. Let it be for
 * performance reasons or simply because the object doesn't expose any useful properties. In those cases it's possible
 * to define alternative comparison strategies, like using the equals method, a comparator or even a custom strategy.
 * These settings can be made for specific nodes or entire types.
 *
 * @author Daniel Bechler
 */
public interface ComparisonConfigurer
{
	Of ofNode(NodePath nodePath);

	Of ofType(Class<?> type);

	OfPrimitiveTypes ofPrimitiveTypes();

	ObjectDifferBuilder and();

	interface Of
	{
		ComparisonConfigurer toUse(ComparisonStrategy comparisonStrategy);

		ComparisonConfigurer toUseEqualsMethod();

		ComparisonConfigurer toUseEqualsMethodOfValueProvidedByMethod(String propertyName);

		ComparisonConfigurer toUseCompareToMethod();
	}

	interface OfPrimitiveTypes
	{
		ComparisonConfigurer toTreatDefaultValuesAs(PrimitiveDefaultValueMode primitiveDefaultValueMode);
	}
}
