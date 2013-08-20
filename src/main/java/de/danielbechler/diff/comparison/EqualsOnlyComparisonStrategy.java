/*
 * Copyright 2013 Daniel Bechler
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

import de.danielbechler.diff.*;
import de.danielbechler.util.*;

import static de.danielbechler.util.Objects.*;

/** @author Daniel Bechler */
public class EqualsOnlyComparisonStrategy implements ComparisonStrategy
{
	private final String equalsValueProviderMethod;

	public EqualsOnlyComparisonStrategy()
	{
		this.equalsValueProviderMethod = null;
	}

	public EqualsOnlyComparisonStrategy(final String equalsValueProviderMethod)
	{
		Assert.hasText(equalsValueProviderMethod, "equalsValueProviderMethod");
		this.equalsValueProviderMethod = equalsValueProviderMethod;
	}

	public void compare(final DiffNode node, final Instances instances)
	{
		if (hasEqual(instances))
		{
			node.setState(DiffNode.State.UNTOUCHED);
		}
		else
		{
			node.setState(DiffNode.State.CHANGED);
		}
	}

	private boolean hasEqual(final Instances instances)
	{
		if (equalsValueProviderMethod != null)
		{
			return instances.access(equalsValueProviderMethod).areEqual();
		}
		else
		{
			return isEqual(instances.getWorking(), instances.getBase());
		}
	}

	public boolean hasValueProviderMethod()
	{
		return equalsValueProviderMethod != null;
	}
}
