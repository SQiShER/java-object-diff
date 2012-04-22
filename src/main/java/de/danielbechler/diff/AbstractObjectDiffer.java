package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.annotation.*;
import de.danielbechler.diff.path.*;

import java.util.*;

/** @author Daniel Bechler */
public abstract class AbstractObjectDiffer
{
	private final Collection<PropertyPath> ignoreProperties = new LinkedHashSet<PropertyPath>(10);
	private final Collection<PropertyPath> equalsOnlyProperties = new LinkedHashSet<PropertyPath>(10);
	private final Collection<Class<?>> equalsOnlyTypes = new LinkedHashSet<Class<?>>(10);

	protected AbstractObjectDiffer()
	{
	}

	protected AbstractObjectDiffer(final AbstractObjectDiffer source)
	{
		equalsOnlyProperties.addAll(source.equalsOnlyProperties);
		equalsOnlyTypes.addAll(source.equalsOnlyTypes);
		ignoreProperties.addAll(source.ignoreProperties);
	}

	public final void addIgnoreProperty(final String property)
	{
		addIgnoreProperty(PropertyPathBuilder.pathOf(property));
	}

	public final void addIgnoreProperty(final PropertyPath selectorPath)
	{
		ignoreProperties.add(selectorPath);
	}

	public final <T> boolean isIgnoreProperty(final Accessor<T> accessor)
	{
		return ignoreProperties.contains(accessor.getPath());
	}

	public final void setEqualsOnlyPaths(final PropertyPath... properties)
	{
		Collections.addAll(equalsOnlyProperties, properties);
	}

	public final void setEqualsOnlyTypes(final Class<?>... types)
	{
		equalsOnlyTypes.addAll(Arrays.asList(types));
	}

	public final void addEqualsOnlyType(final Class<?> type)
	{
		equalsOnlyTypes.add(type);
	}

	public final boolean isEqualsOnlyPathOrType(final PropertyPath propertyPath, final Class<?> propertyType)
	{
		return isEqualsOnlyPath(propertyPath) || isEqualsOnlyType(propertyType);
	}

	public final boolean isEqualsOnlyPath(final PropertyPath selectorPath)
	{
		return equalsOnlyProperties.contains(selectorPath);
	}

	public final boolean isEqualsOnlyType(final Class<?> propertyType)
	{
		return propertyType.getAnnotation(EqualsOnlyType.class) != null || equalsOnlyTypes.contains(propertyType);
	}
}
