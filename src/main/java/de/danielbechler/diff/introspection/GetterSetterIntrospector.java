package de.danielbechler.diff.introspection;

import de.danielbechler.diff.instantiation.TypeInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GetterSetterIntrospector implements Introspector
{
	public TypeInfo introspect(final Class<?> type)
	{
		final TypeInfo typeInfo = new TypeInfo(type);
		for (final Method getter : gettersOf(type))
		{
			if (shouldSkip(getter))
			{
				continue;
			}
			final String propertyName = getPropertyName(getter);
			final Method setter = getCorrespondingSetter(type, getter);
			final PropertyAccessor accessor = new PropertyAccessor(propertyName, getter, setter);
			typeInfo.addPropertyAccessor(accessor);
		}
		return typeInfo;
	}

	private Method getCorrespondingSetter(final Class<?> type, final Method getter)
	{
		final String setterMethodName = getter.getName().replaceAll("^get", "set");
		try
		{
			return type.getMethod(setterMethodName, getter.getReturnType());
		}
		catch (NoSuchMethodException ignored)
		{
			return null;
		}
	}

	private String getPropertyName(final Method getter)
	{
		final StringBuilder sb = new StringBuilder(getter.getName());
		sb.delete(0, 3);
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return sb.toString();
	}

	private List<Method> gettersOf(final Class<?> type)
	{
		final List<Method> filteredMethods = new ArrayList<Method>(type.getMethods().length);
		for (final Method method : type.getMethods())
		{
			final String methodName = method.getName();
			if (!methodName.startsWith("get") || methodName.length() <= 3)
			{
				continue;
			}
			if (method.getGenericParameterTypes().length != 0)
			{
				continue;
			}
			filteredMethods.add(method);
		}
		return filteredMethods;
	}

	@SuppressWarnings("RedundantIfStatement")
	private static boolean shouldSkip(final Method getter)
	{
		if (getter.getName().equals("getClass")) // Java & Groovy
		{
			return true;
		}
		if (getter.getName().equals("getMetaClass")) // Groovy
		{
			return true;
		}
		return false;
	}
}
