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

import de.danielbechler.diff.config.category.CategoryConfiguration;
import de.danielbechler.diff.config.circular.CircularReferenceConfiguration;
import de.danielbechler.diff.config.comparison.ComparisonConfiguration;
import de.danielbechler.diff.config.filtering.ReturnableNodeConfiguration;
import de.danielbechler.diff.config.inclusion.InclusionConfiguration;
import de.danielbechler.diff.config.introspection.IntrospectionConfiguration;

/**
 * Created by Daniel Bechler.
 */
public interface Configuration
{
	ReturnableNodeConfiguration filtering();

	IntrospectionConfiguration introspection();

	CircularReferenceConfiguration circularReferenceHandling();

	InclusionConfiguration<Configuration> inclusion();

	ComparisonConfiguration comparison();

	CategoryConfiguration categories();
}
