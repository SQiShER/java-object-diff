package de.danielbechler.diff.introspection;

import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.selector.BeanPropertyElementSelector;
import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FieldAccessor implements PropertyAwareAccessor
{
	private final Field field;

	FieldAccessor(final Field field)
	{
		Assert.notNull(field, "field");
		this.field = field;
	}

	public Class<?> getType()
	{
		return field.getType();
	}

	public Set<String> getCategoriesFromAnnotation()
	{
		return Collections.emptySet();
	}

	public ElementSelector getElementSelector()
	{
		return new BeanPropertyElementSelector(getPropertyName());
	}

	public Object get(Object target)
	{
		try
		{
			return field.get(target);
		}
		catch (IllegalAccessException e)
		{
			throw new PropertyReadException(getPropertyName(), getType(), e);
		}
	}

	public void set(Object target, Object value)
	{
		try
		{
			field.setAccessible(true);
			field.set(target, value);
		}
		catch (IllegalAccessException e)
		{
			throw new PropertyWriteException(getPropertyName(), getType(), value, e);
		}
		finally
		{
			field.setAccessible(false);
		}
	}

	public void unset(Object target)
	{
	}

	public String getPropertyName()
	{
		return field.getName();
	}

	public Set<Annotation> getFieldAnnotations()
	{
		final Set<Annotation> fieldAnnotations = new HashSet<Annotation>(field.getAnnotations().length);
		fieldAnnotations.addAll(Arrays.asList(field.getAnnotations()));
		return fieldAnnotations;
	}

	public <T extends Annotation> T getFieldAnnotation(Class<T> annotationClass)
	{
		return field.getAnnotation(annotationClass);
	}

	public Set<Annotation> getReadMethodAnnotations()
	{
		return Collections.emptySet();
	}

	public <T extends Annotation> T getReadMethodAnnotation(Class<T> annotationClass)
	{
		return null;
	}

	public boolean isExcludedByAnnotation()
	{
		ObjectDiffProperty annotation = getFieldAnnotation(ObjectDiffProperty.class);
		return annotation != null && annotation.excluded();
	}
}
