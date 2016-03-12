/*
 * Copyright 2016 Daniel Bechler
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

package de.danielbechler.diff.differ;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DifferService implements DifferConfigurer
{
	private final ObjectDifferBuilder objectDifferBuilder;
	private final Collection<DifferFactory> differFactories = new ArrayList<DifferFactory>();

	public DifferService(final ObjectDifferBuilder objectDifferBuilder)
	{
		Assert.notNull(objectDifferBuilder, "objectDifferBuilder");
		this.objectDifferBuilder = objectDifferBuilder;
	}

	public ObjectDifferBuilder register(final DifferFactory differFactory)
	{
		Assert.notNull(differFactory, "differFactory");
		differFactories.add(differFactory);
		return objectDifferBuilder;
	}

	public Collection<DifferFactory> getDifferFactories()
	{
		return Collections.unmodifiableCollection(differFactories);
	}
}
