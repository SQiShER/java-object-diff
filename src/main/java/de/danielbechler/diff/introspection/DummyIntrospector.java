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

import de.danielbechler.diff.instantiation.TypeInfo;
import de.danielbechler.util.Assert;

/**
 * Does not attempt to resolve any accessor of the given type.
 *
 * @author Pascal Brogle
 */
public class DummyIntrospector implements de.danielbechler.diff.introspection.Introspector
{
	public TypeInfo introspect(final Class<?> type)
	{
		Assert.notNull(type, "type");
		return new TypeInfo(type);
	}
}
