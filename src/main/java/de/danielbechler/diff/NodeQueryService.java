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

package de.danielbechler.diff;

import de.danielbechler.diff.category.CategoryResolver;
import de.danielbechler.diff.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.comparison.PrimitiveDefaultValueModeResolver;
import de.danielbechler.diff.filtering.IsReturnableResolver;
import de.danielbechler.diff.inclusion.IsIgnoredResolver;
import de.danielbechler.diff.introspection.IsIntrospectableResolver;

/**
 * Created by Daniel Bechler.
 */
public interface NodeQueryService extends
		CategoryResolver,
		IsIntrospectableResolver,
		IsIgnoredResolver,
		IsReturnableResolver,
		ComparisonStrategyResolver,
		PrimitiveDefaultValueModeResolver
{
}
