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

import de.danielbechler.diff.category.CategoryConfiguration;
import de.danielbechler.diff.category.CategoryService;
import de.danielbechler.diff.circular.CircularReferenceConfiguration;
import de.danielbechler.diff.circular.CircularReferenceService;
import de.danielbechler.diff.comparison.ComparisonConfiguration;
import de.danielbechler.diff.comparison.ComparisonService;
import de.danielbechler.diff.differ.BeanDiffer;
import de.danielbechler.diff.differ.CollectionDiffer;
import de.danielbechler.diff.differ.DifferDispatcher;
import de.danielbechler.diff.differ.DifferProvider;
import de.danielbechler.diff.differ.MapDiffer;
import de.danielbechler.diff.differ.PrimitiveDiffer;
import de.danielbechler.diff.filtering.ReturnableNodeConfiguration;
import de.danielbechler.diff.filtering.ReturnableNodeService;
import de.danielbechler.diff.inclusion.InclusionConfiguration;
import de.danielbechler.diff.inclusion.InclusionService;
import de.danielbechler.diff.introspection.IntrospectionConfiguration;
import de.danielbechler.diff.introspection.IntrospectionService;

/**
 * This is the entry point of every diffing operation. It acts as a factory to get hold of an actual {@link
 * ObjectDiffer} instance and exposes a configuration API to customize its behavior to
 * suit your needs.
 *
 * @author Daniel Bechler
 */
public final class ObjectDifferBuilder
{
	private final Configuration configuration = new ConfigurationImpl();
	private final IntrospectionService introspectionService = new IntrospectionService(configuration);
	private final CategoryService categoryService = new CategoryService();
	private final InclusionService inclusionService = new InclusionService(categoryService, configuration);
	private final ComparisonService comparisonService = new ComparisonService();
	private final ReturnableNodeService returnableNodeService = new ReturnableNodeService();
	private final CircularReferenceService circularReferenceService = new CircularReferenceService();

	private ObjectDifferBuilder()
	{
	}

	public static ObjectDiffer buildDefault()
	{
		return startBuilding().build();
	}

	public ObjectDiffer build()
	{
		final DifferProvider differProvider = new DifferProvider();
		final DifferDispatcher differDispatcher = new DifferDispatcher(differProvider, circularReferenceService, circularReferenceService, inclusionService, returnableNodeService);
		differProvider.push(new BeanDiffer(differDispatcher, introspectionService, returnableNodeService, comparisonService, introspectionService));
		differProvider.push(new CollectionDiffer(differDispatcher, comparisonService));
		differProvider.push(new MapDiffer(differDispatcher, comparisonService));
		differProvider.push(new PrimitiveDiffer(comparisonService));
		return new ObjectDiffer(differDispatcher);
	}

	public static ObjectDifferBuilder startBuilding()
	{
		return new ObjectDifferBuilder();
	}

	/**
	 * Configure the way the ObjectDiffer should behave.
	 */
	public final Configuration configure()
	{
		return configuration;
	}

	public class ConfigurationImpl implements de.danielbechler.diff.Configuration
	{
		private ConfigurationImpl()
		{
		}

		/**
		 * Allows to exclude nodes from being added to the object graph based on criteria that are only known after
		 * the diff for the affected node and all its children has been determined.
		 */
		public ReturnableNodeConfiguration filtering()
		{
			return returnableNodeService;
		}

		/**
		 * Allows to replace the default bean introspector with a custom implementation.
		 */
		public IntrospectionConfiguration introspection()
		{
			return introspectionService;
		}

		/**
		 * Allows to define how the circular reference detector compares object instances.
		 */
		public CircularReferenceConfiguration circularReferenceHandling()
		{
			return circularReferenceService;
		}

		/**
		 * Allows to in- or exclude nodes based on property name, object type, category or location in the object
		 * graph.
		 */
		public InclusionConfiguration inclusion()
		{
			return inclusionService;
		}

		/**
		 * Allows to configure the way objects are compared.
		 */
		public ComparisonConfiguration comparison()
		{
			return comparisonService;
		}

		/**
		 * Allows to assign custom categories (or tags) to entire types or selected elements and properties.
		 */
		public CategoryConfiguration categories()
		{
			return categoryService;
		}
	}
}
