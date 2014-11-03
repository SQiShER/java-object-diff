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

package de.danielbechler.diff.introspection;

import de.danielbechler.diff.instantiation.InstanceFactory;
import de.danielbechler.diff.instantiation.PublicNoArgsConstructorInstanceFactory;
import de.danielbechler.util.Assert;

class InstanceFactoryFallbackDecorator implements InstanceFactory
{
	private final InstanceFactory fallbackInstanceFactory = new PublicNoArgsConstructorInstanceFactory();
	private final InstanceFactory instanceFactory;

	InstanceFactoryFallbackDecorator(final InstanceFactory instanceFactory)
	{
		Assert.notNull(instanceFactory, "instanceFactory");
		this.instanceFactory = instanceFactory;
	}

	public Object newInstanceOfType(final Class<?> type)
	{
		final Object instance = instanceFactory.newInstanceOfType(type);
		if (instance != null)
		{
			return instance;
		}
		else
		{
			return fallbackInstanceFactory.newInstanceOfType(type);
		}
	}
}
