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

import de.danielbechler.util.*;

import java.util.*;

import static de.danielbechler.util.Classes.*;

/** @author Daniel Bechler */
public class DifferFactory
{
	private final Configuration configuration;

	public DifferFactory(final Configuration configuration)
	{
		Assert.notNull(configuration, "configuration");
		this.configuration = configuration;
	}

	public Differ<?> createDiffer(final Class<?> type, final DifferDelegator delegator)
	{
		Assert.notNull(delegator, "delegator");
		if (isPrimitiveType(type))
		{
			return new PrimitiveDiffer(configuration);
		}
		else if (Collection.class.isAssignableFrom(type))
		{
			return new CollectionDiffer(delegator, configuration);
		}
		else if (Map.class.isAssignableFrom(type))
		{
			return new MapDiffer(delegator, configuration);
		}
		else
		{
			return new BeanDiffer(delegator, configuration);
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}
}
