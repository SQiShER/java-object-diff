/*
 * Copyright 2012 Daniel Bechler
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

import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
interface NodeInspector
{
	boolean isIgnored(Node node);

	boolean isIncluded(Node node);

	boolean isExcluded(Node node);

    boolean isCompareToOnly(Node node);

	boolean isEqualsOnly(Node node);
	
	boolean hasEqualsOnlyValueProviderMethod(Node node);
	
	String getEqualsOnlyValueProviderMethod(Node node);

	boolean isReturnable(Node node);

	/**
	 * @return Returns <code>true</code> if the object represented by the given node should be compared via
	 *         introspection. It must always return </code><code>false</code> if {@link
	 *         #isEqualsOnly(de.danielbechler.diff.node.Node)} returns <code>true</code>.
	 */
	boolean isIntrospectible(Node node);
}
