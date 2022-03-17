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

package de.danielbechler.diff.inclusion;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.path.NodePath;

/**
 * Allows to in- or exclude nodes based on property name, object type, category or location in the object graph.
 * Excluded nodes will not be compared, to make sure their accessors won't get called. This is useful in cases where
 * getters could throw exceptions under certain conditions or when certain accessors are expensive to call or simply
 * not relevant for the use-case.
 * <p>
 * In combination with categories this allows to define sub-sets of properties, in order to compare only relevant parts
 * of an object (e.g. exclude all properties marked as _metadata_.)
 *
 * @author Daniel Bechler
 */
@SuppressWarnings("UnusedDeclaration")
public interface InclusionConfigurer
{
	/**
	 * Includes elements (and implicitly all their children) based on certain criteria, unless their parent element
	 * is excluded.
	 */
	ToInclude include();

	ToExclude exclude();

	/**
	 * Registers a custom {@link de.danielbechler.diff.inclusion.InclusionResolver}. Some objects may not be relevant
	 * or suitable for the comparison process. Using an {@link de.danielbechler.diff.inclusion.InclusionResolver} is a
	 * powerful and flexible way to detect and exclude those objects.
	 * <p>
	 * Keep in mind that every single node in the object graph will be checked against each and every registered {@link
	 * de.danielbechler.diff.inclusion.InclusionResolver}. If performance is important to you, make sure that calling
	 * its methods is as cheap as possible.
	 */
	InclusionConfigurer resolveUsing(InclusionResolver resolver);

	ObjectDifferBuilder and();

	public interface ToInclude
	{
		ToInclude category(String category);

		ToInclude type(Class<?> type);

		ToInclude node(NodePath nodePath);

		ToInclude propertyName(String propertyName);

		/**
		 * Include one or more properties of the given type. This automatically causes all other properties of that
		 * type to be implicitly excluded. However, unlike other inclusion mechanisms, this doesn't exclude properties
		 * of other types.
		 *
		 * @param type          The parent type for which the included properties should be specified.
		 * @param propertyNames One or more property names to include.
		 * @see de.danielbechler.diff.introspection.ObjectDiffProperty#inclusion()
		 */
		ToInclude propertyNameOfType(Class<?> type, String... propertyNames);

		InclusionConfigurer also();

		ObjectDifferBuilder and();
	}

	public interface ToExclude
	{
		ToExclude category(String category);

		ToExclude type(Class<?> type);

		ToExclude node(NodePath nodePath);

		ToExclude propertyName(String property);

		/**
		 * Excludes one or more properties of the given type.
		 *
		 * @param type          The parent type for which the excluded properties should be specified.
		 * @param propertyNames One or more property names to exclude.
		 * @see de.danielbechler.diff.introspection.ObjectDiffProperty#inclusion()
		 * @see de.danielbechler.diff.introspection.ObjectDiffProperty#excluded()
		 */
		ToExclude propertyNameOfType(Class<?> type, String... propertyNames);

		InclusionConfigurer also();

		ObjectDifferBuilder and();
	}

}
