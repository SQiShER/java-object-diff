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

import de.danielbechler.diff.category.CategoryConfigurer;
import de.danielbechler.diff.category.CategoryService;
import de.danielbechler.diff.circular.CircularReferenceConfigurer;
import de.danielbechler.diff.circular.CircularReferenceService;
import de.danielbechler.diff.comparison.ComparisonConfigurer;
import de.danielbechler.diff.comparison.ComparisonService;
import de.danielbechler.diff.differ.BeanDiffer;
import de.danielbechler.diff.differ.CollectionDiffer;
import de.danielbechler.diff.differ.Differ;
import de.danielbechler.diff.differ.DifferConfigurer;
import de.danielbechler.diff.differ.DifferDispatcher;
import de.danielbechler.diff.differ.DifferFactory;
import de.danielbechler.diff.differ.DifferProvider;
import de.danielbechler.diff.differ.DifferService;
import de.danielbechler.diff.differ.MapDiffer;
import de.danielbechler.diff.differ.PrimitiveDiffer;
import de.danielbechler.diff.filtering.FilteringConfigurer;
import de.danielbechler.diff.filtering.ReturnableNodeService;
import de.danielbechler.diff.identity.IdentityService;
import de.danielbechler.diff.inclusion.InclusionConfigurer;
import de.danielbechler.diff.inclusion.InclusionService;
import de.danielbechler.diff.introspection.IntrospectionConfigurer;
import de.danielbechler.diff.introspection.IntrospectionService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is the entry point of every diffing operation. It acts as a factory to
 * get hold of an actual {@link ObjectDiffer} instance and exposes a
 * configuration API to customize its behavior to suit your needs.
 *
 * @author Daniel Bechler
 */
public class ObjectDifferBuilder
{
	private final IntrospectionService introspectionService = new IntrospectionService(this);
	private final CategoryService categoryService = new CategoryService(this);
	private final InclusionService inclusionService = new InclusionService(categoryService, this);
	private final ComparisonService comparisonService = new ComparisonService(this);
	private final IdentityService identityService = new IdentityService(this);
	private final ReturnableNodeService returnableNodeService = new ReturnableNodeService(this);
	private final CircularReferenceService circularReferenceService = new CircularReferenceService(this);
	private final DifferService differService = new DifferService(this);
	private final NodeQueryService nodeQueryService;

	private ObjectDifferBuilder()
	{
		nodeQueryService = new DefaultNodeQueryService(categoryService,
				introspectionService,
				inclusionService,
				returnableNodeService,
				comparisonService,
				comparisonService);
	}

	/**
	 * Allows to exclude nodes from being added to the object graph based on
	 * criteria that are only known after the diff for the affected node and all
	 * its children has been determined.
	 */
	public FilteringConfigurer filtering()
	{
		return returnableNodeService;
	}

	/**
	 * Allows to replace the default bean introspector with a custom
	 * implementation.
	 */
	public IntrospectionConfigurer introspection()
	{
		return introspectionService;
	}

	/**
	 * Allows to define how the circular reference detector compares object
	 * instances.
	 */
	public CircularReferenceConfigurer circularReferenceHandling()
	{
		return circularReferenceService;
	}

	/**
	 * Allows to in- or exclude nodes based on property name, object type,
	 * category or location in the object graph.
	 */
	public InclusionConfigurer inclusion()
	{
		return inclusionService;
	}

	/**
	 * Allows to configure the way objects are compared.
	 */
	public ComparisonConfigurer comparison()
	{
		return comparisonService;
	}

	/**
	 * <b>EXPERT FEATURE</b>: Allows to configure the way the identity of objects is determined in order to establish
	 * the relationship between different versions. By default, this is done via the <code>equals</code> method; but
	 * sometimes that's just not possible. For example when you are dealing with generated classes or you are dealing
	 * with third-party code.
	 * <p>
	 * Please keep in mind that this only alters the way this library establishes the connection between two objects.
	 * This doesn't extend to the underlying collections. So keep what in mind when you start merging your collections
	 * and weird things start to happen.
	 * <p>
	 * <b>WARNING</b>: Personally I'd try to avoid this feature as long as possible and only use it when there is
	 * absolutely no other way.
	 */
	public IdentityService identity()
	{
		return identityService;
	}

	/**
	 * Allows to assign custom categories (or tags) to entire types or selected
	 * elements and properties.
	 */
	public CategoryConfigurer categories()
	{
		return categoryService;
	}

	public DifferConfigurer differs()
	{
		return differService;
	}

	public static ObjectDiffer buildDefault()
	{
		return startBuilding().build();
	}

	public static ObjectDifferBuilder startBuilding()
	{
		return new ObjectDifferBuilder();
	}

	public ObjectDiffer build()
	{
		final DifferProvider differProvider = new DifferProvider();
		final DifferDispatcher differDispatcher = newDifferDispatcher(differProvider);
		differProvider.push(newBeanDiffer(differDispatcher));
		differProvider.push(newCollectionDiffer(differDispatcher));
		differProvider.push(newMapDiffer(differDispatcher));
		differProvider.push(newPrimitiveDiffer());
		differProvider.pushAll(createCustomDiffers(differDispatcher));
		return new ObjectDiffer(differDispatcher);
	}

	private DifferDispatcher newDifferDispatcher(final DifferProvider differProvider)
	{
		return new DifferDispatcher(
				differProvider,
				circularReferenceService,
				circularReferenceService,
				inclusionService,
				returnableNodeService,
				introspectionService,
				categoryService);
	}

	private Differ newBeanDiffer(final DifferDispatcher differDispatcher)
	{
		return new BeanDiffer(
				differDispatcher,
				introspectionService,
				returnableNodeService,
				comparisonService,
				introspectionService);
	}

	private Differ newCollectionDiffer(final DifferDispatcher differDispatcher)
	{
		return new CollectionDiffer(differDispatcher, comparisonService, identityService);
	}

	private Differ newMapDiffer(final DifferDispatcher differDispatcher)
	{
		return new MapDiffer(differDispatcher, comparisonService);
	}

	private Differ newPrimitiveDiffer()
	{
		return new PrimitiveDiffer(comparisonService);
	}

	private Iterable<Differ> createCustomDiffers(final DifferDispatcher differDispatcher)
	{
		final Collection<DifferFactory> differFactories = differService.getDifferFactories();
		final Collection<Differ> differs = new ArrayList<Differ>(differFactories.size());
		for (final DifferFactory differFactory : differFactories)
		{
			differs.add(differFactory.createDiffer(differDispatcher, nodeQueryService));
		}
		return differs;
	}
}
