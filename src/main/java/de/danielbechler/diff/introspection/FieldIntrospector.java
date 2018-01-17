package de.danielbechler.diff.introspection;

import de.danielbechler.diff.instantiation.TypeInfo;

import java.lang.reflect.*;

public class FieldIntrospector implements Introspector
{
    private boolean returnFinalFields;

    public TypeInfo introspect(final Class<?> type)
    {
        final TypeInfo typeInfo = new TypeInfo(type);
        for (final Field field : type.getFields())
        {
            if (shouldSkip(field))
            {
                continue;
            }
            typeInfo.addPropertyAccessor(new FieldAccessor(field));
        }
        return typeInfo;
    }

    private boolean shouldSkip(final Field field)
    {
        return Modifier.isFinal(field.getModifiers()) && !returnFinalFields;
    }

    public void setReturnFinalFields(final boolean returnFinalFields)
    {
        this.returnFinalFields = returnFinalFields;
    }
}
