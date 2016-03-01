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

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.instantiation.InstanceFactory;
import de.danielbechler.diff.instantiation.PublicNoArgsConstructorInstanceFactory;
import de.danielbechler.diff.instantiation.TypeInfo;
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
public class IntrospectionService implements IntrospectionConfigurer, IsIntrospectableResolver, TypeInfoResolver, PropertyAccessExceptionHandlerResolver
{
	private final Map<Class<?>, Introspector> typeIntrospectorMap = new HashMap<Class<?>, Introspector>();
	private final Map<Class<?>, IntrospectionMode> typeIntrospectionModeMap = new HashMap<Class<?>, IntrospectionMode>();
	private final NodePathValueHolder<Introspector> nodePathIntrospectorHolder = new NodePathValueHolder<Introspector>();
	private final NodePathValueHolder<IntrospectionMode> nodePathIntrospectionModeHolder = new NodePathValueHolder<IntrospectionMode>();
	private final ObjectDifferBuilder objectDifferBuilder;
	private Introspector defaultIntrospector;
	private InstanceFactory instanceFactory = new PublicNoArgsConstructorInstanceFactory();
	private PropertyAccessExceptionHandler defaultPropertyAccessExceptionHandler = new DefaultPropertyAccessExceptionHandler();

	public IntrospectionService(final ObjectDifferBuilder objectDifferBuilder)
	{
		this.objectDifferBuilder = objectDifferBuilder;
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

	public PropertyAccessExceptionHandler resolvePropertyAccessExceptionHandler(final Class<?> parentType, final String propertyName)
	{
		return defaultPropertyAccessExceptionHandler;
	}

	public TypeInfo typeInfoForNode(final DiffNode node)
	{
		final Class<?> beanType = node.getValueType();
		final Introspector introspector = introspectorForNode(node);
		final TypeInfo typeInfo = introspector.introspect(beanType);
		typeInfo.setInstanceFactory(instanceFactory);
		return typeInfo;
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

		if (defaultIntrospector == null)
		{
			defaultIntrospector = new StandardIntrospector();
		}
		return defaultIntrospector;
	}

	public IntrospectionConfigurer setInstanceFactory(final InstanceFactory instanceFactory)
	{
		Assert.notNull(instanceFactory, "instanceFactory");
		this.instanceFactory = new InstanceFactoryFallbackDecorator(instanceFactory);
		return this;
	}

	public IntrospectionConfigurer setDefaultIntrospector(final Introspector introspector)
	{
		Assert.notNull(introspector, "The default introspector must not be null");
		defaultIntrospector = introspector;
		return this;
	}

	public IntrospectionConfigurer handlePropertyAccessExceptionsUsing(final PropertyAccessExceptionHandler exceptionHandler)
	{
		Assert.notNull(exceptionHandler, "exceptionHandler");
		defaultPropertyAccessExceptionHandler = exceptionHandler;
		return this;
	}

	public Of ofType(final Class<?> type)
	{
		return new Of()
		{
			public IntrospectionConfigurer toUse(final Introspector introspector)
			{
				typeIntrospectorMap.put(type, introspector);
				return IntrospectionService.this;
			}

			public IntrospectionConfigurer toBeEnabled()
			{
				typeIntrospectionModeMap.put(type, IntrospectionMode.ENABLED);
				return IntrospectionService.this;
			}

			public IntrospectionConfigurer toBeDisabled()
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
			public IntrospectionConfigurer toUse(final Introspector introspector)
			{
				nodePathIntrospectorHolder.put(path, introspector);
				return IntrospectionService.this;
			}

			public IntrospectionConfigurer toBeEnabled()
			{
				nodePathIntrospectionModeHolder.put(path, IntrospectionMode.ENABLED);
				return IntrospectionService.this;
			}

			public IntrospectionConfigurer toBeDisabled()
			{
				nodePathIntrospectionModeHolder.put(path, IntrospectionMode.DISABLED);
				return IntrospectionService.this;
			}
		};
	}

	public ObjectDifferBuilder and()
	{
		return objectDifferBuilder;
	}

	public enum IntrospectionMode
	{
		ENABLED,
		DISABLED
	}
}
