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

import de.danielbechler.diff.node.DiffNode;

/**
 * This can be used to implement custom inclusion mechanisms. Some objects may not be relevant or suitable for the
 * comparison process. Using an {@link de.danielbechler.diff.inclusion.InclusionResolver} is a powerful and flexible
 * way to detect and exclude those objects.
 * <p>
 * Keep in mind that every single node in the object graph will be checked against each and every registered {@link
 * de.danielbechler.diff.inclusion.InclusionResolver}. If performance is important to you, make sure that calling its
 * methods is as cheap as possible.
 */
public interface InclusionResolver
{
	/**
	 * Determines whether a given {@link de.danielbechler.diff.node.DiffNode} should be included into the comparison
	 * process.
	 *
	 * @param node The node to determine the inclusion for. Keep in mind that the {@link
	 *             de.danielbechler.diff.node.DiffNode} doesn't contain any children at this point and
	 *             albeit it is already linked to its parent node, the parent node also probably hasn't been fully
	 *             processed yet. It is only safe to examine the node path and type related properties along the path
	 *             up to the root node, but definitely not to make any decisions based on the state or number of child
	 *             nodes.
	 * @return Returns either {@link de.danielbechler.diff.inclusion.Inclusion#INCLUDED} to indicate an explicit
	 * inclusion, {@link de.danielbechler.diff.inclusion.Inclusion#EXCLUDED} to inidicate an explicit exclusion or
	 * {@link de.danielbechler.diff.inclusion.Inclusion#DEFAULT} in case this resolver doesn't want to influence the
	 * decision. This method should never return <code>null</code>.
	 */
	Inclusion getInclusion(DiffNode node);

	/**
	 * When this method returns <code>true</code>, it causes the inclusion service to exclude all nodes that are not
	 * explicitly included via {@link de.danielbechler.diff.inclusion.Inclusion#INCLUDED}. Otherwise nodes with {@link
	 * de.danielbechler.diff.inclusion.Inclusion#DEFAULT} will also be included.
	 */
	boolean enablesStrictIncludeMode();
}
