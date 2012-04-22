package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

/** @author Daniel Bechler */
@SuppressWarnings({"unchecked"})
public final class PropertyAccessor<T> extends AbstractAccessor<T>
{
	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	private final String propertyName;
	private final Class<T> type;
	private final Method readMethod;
	private final Method writeMethod;

	public PropertyAccessor(final String propertyName, final Method readMethod, final Method writeMethod)
	{
		Assert.notNull(propertyName, "propertyName");
		Assert.notNull(readMethod, "readMethod");
		this.propertyName = propertyName;
		this.readMethod = makeAccessible(readMethod);
		this.writeMethod = makeAccessible(writeMethod);
		this.type = (Class<T>) this.readMethod.getReturnType();
	}

	private static Method makeAccessible(final Method method)
	{
		if (method != null)
		{
			// I'm not sure, why I suddenly need this, but since I integrated the standard Java Introspector, I do.
			method.setAccessible(true);
		}
		return method;
	}

	public void set(final Object target, final Object value)
	{
		if (target == null)
		{
			logger.debug("The target object is null");
			logFailedSet(value);
		}
		else if (writeMethod == null)
		{
			logger.debug("No setter found for property '{}'", propertyName);
			tryToReplaceContentOfCollectionTypes(target, value);
		}
		else
		{
			invokeWriteMethod(target, value);
		}
	}

	private void logFailedSet(final Object value)
	{
		logger.info("Couldn't set new value '{}' for property '{}'", value, propertyName);
	}

	private void invokeWriteMethod(final Object target, final Object value)
	{
		try
		{
			writeMethod.invoke(target, value);
		}
		catch (Exception e)
		{
			logFailedSet(value);

			final PropertyWriteException ex = new PropertyWriteException(e);
			ex.setPropertyName(propertyName);
			ex.setTargetType(getType());
			throw ex;
		}
	}

	private void tryToReplaceContentOfCollectionTypes(final Object target, final Object value)
	{
		if (Collection.class.isAssignableFrom(readMethod.getReturnType()))
		{
			tryToReplaceCollectionContent((Collection) get(target), (Collection) value);
			return;
		}

		if (Map.class.isAssignableFrom(readMethod.getReturnType()))
		{
			tryToReplaceMapContent((Map) get(target), (Map) value);
			return;
		}

		logFailedSet(value);
	}

	private static boolean tryToReplaceCollectionContent(final Collection target, final Collection value)
	{
		if (target == null)
		{
			return false;
		}
		try
		{
			target.clear();
			target.addAll(value);
			return true;
		}
		catch (Exception unmodifiable)
		{
			logger.debug("Failed to replace content of existing Collection", unmodifiable);
			return false;
		}
	}

	private static boolean tryToReplaceMapContent(final Map target, final Map value)
	{
		if (target == null)
		{
			return false;
		}
		try
		{
			target.clear();
			target.putAll(value);
			return true;
		}
		catch (Exception unmodifiable)
		{
			logger.debug("Failed to replace content of existing Map", unmodifiable);
			return false;
		}
	}

	public T get(final Object target)
	{
		if (target == null)
		{
			return null;
		}
		try
		{
			return (T) readMethod.invoke(target);
		}
		catch (Exception e)
		{
			final PropertyReadException ex = new PropertyReadException(e);
			ex.setPropertyName(propertyName);
			ex.setTargetType(target.getClass());
			throw ex;
		}
	}

	public void unset(final Object target, final Object value)
	{
		set(target, value);
	}

	public Class<T> getType()
	{
		return this.type;
	}

	public String getPropertyName()
	{
		return this.propertyName;
	}

	public PropertyPath.Element toPathElement()
	{
		return new NamedPropertyElement(this.propertyName);
	}

	public PropertyPath getPath()
	{
		return new PropertyPath(toPathElement());
	}
}
