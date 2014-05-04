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

import de.danielbechler.diff.Configuration;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.path.NodePathValueHolder;
import de.danielbechler.util.Assert;
import de.danielbechler.util.Classes;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Bechler
 */
public class IntrospectionService implements IntrospectionConfiguration, IsIntrospectableResolver, IntrospectorResolver
{
	private final Map<Class<?>, Introspector> typeIntrospectorMap = new HashMap<Class<?>, Introspector>();
	private final Map<Class<?>, IntrospectionMode> typeIntrospectionModeMap = new HashMap<Class<?>, IntrospectionMode>();
	private final NodePathValueHolder<Introspector> nodePathIntrospectorHolder = new NodePathValueHolder<Introspector>();
	private final NodePathValueHolder<IntrospectionMode> nodePathIntrospectionModeHolder = new NodePathValueHolder<IntrospectionMode>();
	private final Configuration configuration;
	private Introspector defaultIntrospector = new StandardBeanIntrospector();

	public IntrospectionService(final Configuration configuration)
	{
		this.configuration = configuration;
	}

	public boolean isIntrospectable(final DiffNode node)
	{
		final Class<?> nodeType = node.getValueType();
		if (nodeType == null)
		{
			return false;
		}
		else if (isPrimitiveTypeEnumOrArray(nodeType))
		{
			return false;
		}
		else if (nodePathIntrospectionModeHolder.valueForNodePath(node.getPath()) == IntrospectionMode.DISABLED)
		{
			return false;
		}
		else if (typeIntrospectionModeMap.get(nodeType) == IntrospectionMode.DISABLED)
		{
			return false;
		}
		return true;
	}

	private static boolean isPrimitiveTypeEnumOrArray(final Class<?> nodeType)
	{
		return Classes.isPrimitiveType(nodeType)
				|| Classes.isPrimitiveWrapperType(nodeType)
				|| nodeType.isEnum()
				|| nodeType.isArray();
	}

	public Introspector introspectorForNode(final DiffNode node)
	{
		final Introspector typeIntrospector = typeIntrospectorMap.get(node.getValueType());
		if (typeIntrospector != null)
		{
			return typeIntrospector;
		}

		final Introspector nodePathIntrospector = nodePathIntrospectorHolder.valueForNodePath(node.getPath());
		if (nodePathIntrospector != null)
		{
			return nodePathIntrospector;
		}

		return defaultIntrospector;
	}

	public IntrospectionConfiguration setDefaultIntrospector(final Introspector introspector)
	{
		Assert.notNull(introspector, "The default introspector must not be null");
		defaultIntrospector = introspector;
		return this;
	}

	public Of ofType(final Class<?> type)
	{
		return new Of()
		{
			public IntrospectionConfiguration toUse(final Introspector introspector)
			{
				typeIntrospectorMap.put(type, introspector);
				return IntrospectionService.this;
			}

			public IntrospectionConfiguration toBeEnabled()
			{
				typeIntrospectionModeMap.put(type, IntrospectionMode.ENABLED);
				return IntrospectionService.this;
			}

			public IntrospectionConfiguration toBeDisabled()
			{
				typeIntrospectionModeMap.put(type, IntrospectionMode.DISABLED);
				return IntrospectionService.this;
			}
		};
	}

	public Of ofNode(final NodePath path)
	{
		return new Of()
		{
			public IntrospectionConfiguration toUse(final Introspector introspector)
			{
				nodePathIntrospectorHolder.put(path, introspector);
				return IntrospectionService.this;
			}

			public IntrospectionConfiguration toBeEnabled()
			{
				nodePathIntrospectionModeHolder.put(path, IntrospectionMode.ENABLED);
				return IntrospectionService.this;
			}

			public IntrospectionConfiguration toBeDisabled()
			{
				nodePathIntrospectionModeHolder.put(path, IntrospectionMode.DISABLED);
				return IntrospectionService.this;
			}
		};
	}

	public Configuration and()
	{
		return configuration;
	}

	public enum IntrospectionMode
	{
		ENABLED,
		DISABLED
	}
}
