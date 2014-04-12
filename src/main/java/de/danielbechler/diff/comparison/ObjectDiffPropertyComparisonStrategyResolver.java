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

import de.danielbechler.diff.introspection.ObjectDiffEqualsOnlyType;
import de.danielbechler.diff.introspection.ObjectDiffProperty;
import de.danielbechler.util.Strings;

/**
 * Created by Daniel Bechler.
 */
public class ObjectDiffPropertyComparisonStrategyResolver
{
	public static final ObjectDiffPropertyComparisonStrategyResolver instance;

	static
	{
		instance = new ObjectDiffPropertyComparisonStrategyResolver();
	}

	private ObjectDiffPropertyComparisonStrategyResolver()
	{
	}

	@SuppressWarnings("MethodMayBeStatic")
	public ComparisonStrategy comparisonStrategyForAnnotation(final ObjectDiffProperty annotation)
	{
		if (annotation == null || !annotation.equalsOnly())
		{
			return null;
		}
		if (Strings.hasText(annotation.equalsOnlyValueProviderMethod()))
		{
			return new EqualsOnlyComparisonStrategy(annotation.equalsOnlyValueProviderMethod());
		}
		return new EqualsOnlyComparisonStrategy();
	}

	@SuppressWarnings("MethodMayBeStatic")
	public ComparisonStrategy comparisonStrategyForAnnotation(final ObjectDiffEqualsOnlyType annotation)
	{
		if (annotation == null)
		{
			return null;
		}
		if (Strings.hasText(annotation.valueProviderMethod()))
		{
			return new EqualsOnlyComparisonStrategy(annotation.valueProviderMethod());
		}
		return new EqualsOnlyComparisonStrategy();
	}
}
