package de.danielbechler.diff.introspection;

import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.instantiation.TypeInfo;

import java.util.Collection;

public class DefaultIntrospector implements Introspector
{
	private final FieldIntrospector fieldIntrospector = new FieldIntrospector();
	private final GetterSetterIntrospector getterSetterIntrospector = new GetterSetterIntrospector();
	private boolean returnFields = false;

	public TypeInfo introspect(final Class<?> type)
	{
		final TypeInfo typeInfo = new TypeInfo(type);
		if (returnFields)
		{
			final Collection<PropertyAwareAccessor> fieldAccessors = fieldIntrospector.introspect(type).getAccessors();
			for (final PropertyAwareAccessor fieldAccessor : fieldAccessors)
			{
				typeInfo.addPropertyAccessor(fieldAccessor);
			}
		}
		final Collection<PropertyAwareAccessor> getterSetterAccessors = getterSetterIntrospector.introspect(type).getAccessors();
		for (final PropertyAwareAccessor getterSetterAccessor : getterSetterAccessors)
		{
			typeInfo.addPropertyAccessor(getterSetterAccessor);
		}
		return typeInfo;
	}

	public void setReturnFields(final boolean returnFields)
	{
		this.returnFields = returnFields;
	}

	public void setReturnFinalFields(final boolean returnFinalFields)
	{
		fieldIntrospector.setReturnFinalFields(returnFinalFields);
	}
}
