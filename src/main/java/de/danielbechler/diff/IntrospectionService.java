package de.danielbechler.diff;

import de.danielbechler.diff.bean.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
class IntrospectionService implements IntrospectionConfiguration, IsIntrospectableResolver, IntrospectorResolver
{
	private final Map<Class<?>, Introspector> typeIntrospectorMap = new HashMap<Class<?>, Introspector>();
	private final Map<Class<?>, IntrospectionMode> typeIntrospectionModeMap = new HashMap<Class<?>, IntrospectionMode>();
	private final NodePathValueHolder<Introspector> nodePathIntrospectorHolder = new NodePathValueHolder<Introspector>();
	private final NodePathValueHolder<IntrospectionMode> nodePathIntrospectionModeHolder = new NodePathValueHolder<IntrospectionMode>();
	private Introspector defaultIntrospector = new StandardBeanIntrospector();

	public enum IntrospectionMode
	{
		ENABLED,
		DISABLED
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

	private static boolean isPrimitiveTypeEnumOrArray(final Class<?> nodeType)
	{
		return Classes.isPrimitiveType(nodeType)
				|| Classes.isPrimitiveWrapperType(nodeType)
				|| nodeType.isEnum()
				|| nodeType.isArray();
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
}
