package de.danielbechler.diff;

import de.danielbechler.diff.annotation.*;
import de.danielbechler.diff.path.*;

import java.util.*;

/** @author Daniel Bechler */
public class Configuration
{
	private Collection<PropertyPath> ignoreProperties = new LinkedHashSet<PropertyPath>(10);
	private Collection<PropertyPath> equalsOnlyProperties = new LinkedHashSet<PropertyPath>(10);
	private Collection<Class<?>> equalsOnlyTypes = new LinkedHashSet<Class<?>>(10);

	public Collection<PropertyPath> getIgnoreProperties()
	{
		return Collections.unmodifiableCollection(ignoreProperties);
	}

	public void setIgnoreProperties(final Collection<PropertyPath> ignoreProperties)
	{
		this.ignoreProperties = ignoreProperties;
	}

	public void addIgnoreProperty(final PropertyPath propertyPath)
	{
		this.ignoreProperties.add(propertyPath);
	}

	public Collection<PropertyPath> getEqualsOnlyProperties()
	{
		return Collections.unmodifiableCollection(equalsOnlyProperties);
	}

	public void setEqualsOnlyProperties(final Collection<PropertyPath> equalsOnlyProperties)
	{
		this.equalsOnlyProperties = equalsOnlyProperties;
	}

	public void addEqualsOnlyProperty(final PropertyPath propertyPath)
	{
		this.equalsOnlyProperties.add(propertyPath);
	}

	public Collection<Class<?>> getEqualsOnlyTypes()
	{
		return Collections.unmodifiableCollection(equalsOnlyTypes);
	}

	public void setEqualsOnlyTypes(final Collection<Class<?>> equalsOnlyTypes)
	{
		this.equalsOnlyTypes = equalsOnlyTypes;
	}

	public void addEqualsOnlyType(final Class<?> type)
	{
		this.equalsOnlyTypes.add(type);
	}

	public boolean isIgnored(final PropertyPath propertyPath)
	{
		return ignoreProperties.contains(propertyPath);
	}

	public boolean isEqualsOnly(final PropertyPath propertyPath, final Class<?> propertyType)
	{
		if (isEqualsOnlyPath(propertyPath))
		{
			return true;
		}
		else if (isEqualsOnlyType(propertyType))
		{
			return true;
		}
		return false;
	}

	public boolean isEqualsOnlyPath(final PropertyPath selectorPath)
	{
		return equalsOnlyProperties.contains(selectorPath);
	}

	public boolean isEqualsOnlyType(final Class<?> propertyType)
	{
		if (propertyType.getAnnotation(ObjectDiffEqualsOnlyType.class) != null)
		{
			return true;
		}
		else if (equalsOnlyTypes.contains(propertyType))
		{
			return true;
		}
		return false;
	}
}
