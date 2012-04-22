package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.introspect.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
public final class BeanDiffer extends AbstractObjectDiffer implements ObjectDiffer
{
	private final Introspector introspector = new StandardIntrospector();

	@SuppressWarnings({"unchecked"})
	public <T> DiffNode<T> compare(final T modifiedInstance, final T baseInstance)
	{
		Assert.notNull(modifiedInstance, "Missing argument [modifiedInstance]");
//		Assert.notNull(baseInstance, "Missing argument [baseInstance]");
		if (baseInstance != null && !modifiedInstance.getClass().equals(baseInstance.getClass()))
		{
			throw new IllegalArgumentException("Comparison of different types is not (yet) supported. " +
													   "Modified was:" + modifiedInstance.getClass() + "; " +
													   "Base was: " + baseInstance.getClass());
		}
		final Accessor<T> accessor = new RootAccessor<T>();
		final T defaultInstance = (T) Classes.freshInstanceOf(modifiedInstance.getClass());
		return compare(modifiedInstance, baseInstance, defaultInstance, accessor);
	}

	public DiffNode compare(final Instances instances, final Accessor accessor)
	{
		return null;
	}

	public DiffNode compare(final Object modifiedInstance,
							final Object baseInstance,
							final Object defaultInstance,
							final Accessor accessor)
	{
		final Accessor childAccessor;
		if (accessor instanceof ChainingAccessor)
		{
			childAccessor = ((ChainingAccessor) accessor).getDetachedChildAccessor();
		}
		else
		{
			childAccessor = accessor;
		}
		final Object baseValue = childAccessor.get(baseInstance);
		final Object modifiedValue = childAccessor.get(modifiedInstance);
		final Object defaultValue = childAccessor.get(defaultInstance);
		final PropertyPath selectorPath = accessor.getPath();
		if (isIgnoreProperty(accessor))
		{
			final DiffNode difference = new DefaultNode(childAccessor);
			difference.setType(DifferenceType.UNTOUCHED);
			return difference;
		}
		final Class<?> propertyType = safeTypeOf(childAccessor, baseValue, modifiedValue, defaultValue);
		if (Collection.class.isAssignableFrom(propertyType))
		{
			final Collection modifiedCollection = (Collection) modifiedValue;
			final Collection baseCollection = (Collection) baseValue;
			final Collection defaultCollection = (Collection) defaultValue;
			return new CollectionDiffer(this).compare(modifiedCollection, baseCollection, defaultCollection, accessor);
		}
		else if (Map.class.isAssignableFrom(propertyType))
		{
			return new MapDiffer(this).compare(
					modifiedValue != null ? Map.class.cast(modifiedValue) : null,
					baseValue != null ? Map.class.cast(baseValue) : null,
					defaultValue != null ? Map.class.cast(defaultValue) : null,
					accessor
			);
		}
		else if (propertyType.isArray())
		{
			// TODO
		}
		final DiffNode difference = new DefaultNode(childAccessor);
		difference.setType(DifferenceType.UNTOUCHED);
		if (hasBeenAdded(defaultValue, baseValue, modifiedValue))
		{
			difference.setType(DifferenceType.ADDED);
		}
		else if (hasBeenRemoved(defaultValue, baseValue, modifiedValue))
		{
			difference.setType(DifferenceType.REMOVED);
		}
		else if (modifiedValue == baseValue) // both null or both same instance
		{
			return difference;
		}
		else
		{
			if (Classes.isSimpleType(propertyType) || isEqualsOnlyPath(selectorPath) || isEqualsOnlyType(propertyType))
			{
				if (!Objects.isEqual(baseValue, modifiedValue))
				{
					difference.setType(DifferenceType.CHANGED);
				}
			}
			else
			{
				for (final Accessor<?> typeAccessor : introspect(propertyType))
				{
					final Accessor<Object> chain = new ChainedAccessor(accessor, typeAccessor);
					final DiffNode<Object> node = compare(modifiedValue, baseValue, defaultValue, chain);
					if (node.isDifferent())
					{
						difference.addChild(node);
					}
				}
				if (difference.hasChildren())
				{
					difference.setType(DifferenceType.CHANGED);
				}
			}
		}
		return difference;
	}

	private static Class<?> safeTypeOf(final Accessor<?> accessor,
									   final Object baseValue,
									   final Object modifiedValue,
									   final Object defaultValue)
	{
		if (accessor instanceof PropertyAccessor)
		{
			return ((PropertyAccessor) accessor).getType();
		}
		return typeOf(baseValue, modifiedValue, defaultValue);
	}

	@SuppressWarnings({"unchecked"})
	private static <T> Class<T> typeOf(final T... values)
	{
		for (final T value : values)
		{
			if (value != null)
			{
				return (Class<T>) value.getClass();
			}
		}
		return (Class<T>) Object.class;
//		throw new RuntimeException("Couldn't extract object type");
	}

	private static boolean hasBeenRemoved(final Object defaultValue, final Object baseValue, final Object updatedValue)
	{
		if (baseValue != null && updatedValue == null && !Classes.isSimpleType(baseValue.getClass()))
		{
			return true;
		}
		return Objects.isEqual(defaultValue, updatedValue) && !Objects.isEqual(baseValue, updatedValue);
	}

	private static boolean hasBeenAdded(final Object defaultValue, final Object baseValue, final Object updatedValue)
	{
		// Not sure if the commented part is needed. Tests succeed without it... but not everything is tested yet.
		if (baseValue == null && updatedValue != null) // && !Classes.isSimpleType(updatedValue.getClass()))
		{
			return true;
		}
		return Objects.isEqual(defaultValue, baseValue) && !Objects.isEqual(baseValue, updatedValue);
	}

	private Iterable<Accessor<?>> introspect(final Class<?> type)
	{
		return introspector.introspect(type);
	}
}
