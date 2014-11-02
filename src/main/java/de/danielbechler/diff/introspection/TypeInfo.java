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

import de.danielbechler.diff.access.PropertyAwareAccessor;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Daniel Bechler.
 */
public class TypeInfo
{
	private final Class<?> type;
	private final Collection<PropertyAwareAccessor> propertyAwareAccessors = new LinkedList<PropertyAwareAccessor>();
	private InstanceFactory instanceFactory;

	public TypeInfo(final Class<?> type)
	{
		this.type = type;
	}

	public void addPropertyAccessor(final PropertyAwareAccessor propertyAccessor)
	{
		propertyAwareAccessors.add(propertyAccessor);
	}

	public Class<?> getType()
	{
		return type;
	}

	public Object newInstance()
	{
		return instanceFactory.newInstanceOfType(type);
	}

	public Collection<PropertyAwareAccessor> getAccessors()
	{
		return propertyAwareAccessors;
	}

	void setInstanceFactory(final InstanceFactory instanceFactory)
	{
		this.instanceFactory = instanceFactory;
	}
}
