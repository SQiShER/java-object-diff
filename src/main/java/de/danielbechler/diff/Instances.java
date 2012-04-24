package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;
import de.danielbechler.util.Collections;

import java.util.*;

/** @author Daniel Bechler */
class Instances
{
	private final Accessor sourceAccessor;
	private final Object working;
	private final Object base;
	private final Object fresh;

	public static <T> Instances of(final Accessor sourceAccessor, final T working, final T base, final T fresh)
	{
		return new Instances(sourceAccessor, working, base, fresh);
	}

	public static <T> Instances of(final Accessor sourceAccessor, final T working, final T base)
	{
		final Object fresh = (working != null) ? Classes.freshInstanceOf(working.getClass()) : null;
		return new Instances(sourceAccessor, working, base, fresh);
	}

	public static <T> Instances of(final T working, final T base)
	{
		final Object fresh = (working != null) ? Classes.freshInstanceOf(working.getClass()) : null;
		return new Instances(new RootAccessor(), working, base, fresh);
	}

	private Instances(final Accessor sourceAccessor,
					  final Object working,
					  final Object base,
					  final Object fresh)
	{
		Assert.notNull(sourceAccessor, "sourceAccessor");
		this.sourceAccessor = sourceAccessor;
		this.working = working;
		this.base = base;
		this.fresh = fresh;
	}

	public Accessor getSourceAccessor()
	{
		return sourceAccessor;
	}

	public Instances access(final Accessor accessor)
	{
		Assert.notNull(accessor, "accessor");
		return new Instances(accessor, accessor.get(working), accessor.get(base), accessor.get(fresh));
	}

	public Object getWorking()
	{
		return working;
	}

	public <T> T getWorking(final Class<T> type)
	{
		return working != null ? type.cast(working) : null;
	}

	public Object getBase()
	{
		return base;
	}

	public <T> T getBase(final Class<T> type)
	{
		return base != null ? type.cast(base) : null;
	}

	public Object getFresh()
	{
		return fresh;
	}

	public <T> T getFresh(final Class<T> type)
	{
		return fresh != null ? type.cast(fresh) : null;
	}

	public boolean hasBeenAdded()
	{
		if (working != null && base == null)
		{
			return true;
		}
		if (Objects.isEqual(fresh, base) && !Objects.isEqual(base, working))
		{
			return true;
		}
		return false;
	}

	public boolean hasBeenRemoved()
	{
		if (base != null && working == null)
		{
			return true;
		}
		if (Objects.isEqual(fresh, working) && !Objects.isEqual(base, working))
		{
			return true;
		}
		return false;
	}

	public boolean areEqual()
	{
		return Objects.isEqual(base, working);
	}

	public boolean areSame()
	{
		return working == base;
	}

	public Class<?> getType()
	{
		if (sourceAccessor instanceof TypeAwareAccessor)
		{
			return ((TypeAwareAccessor) sourceAccessor).getType();
		}
		final Set<Class<?>> types = Classes.typesOf(working, base, fresh);
		if (types.isEmpty())
		{
			return null;
		}
		if (types.size() == 1)
		{
			return Collections.firstElementOf(types);
		}
		throw new IllegalStateException("Detected instances of different types " + types + ". " +
												"Instances must either be null or have the exact same type.");
		// NOTE It would be nice to be able to define a least common denominator like Map or Collection to allow mixed types
	}

	public PropertyPath getPropertyPath(final Node parentNode)
	{
		return new PropertyPathBuilder()
				.withPropertyPath(parentNode != null ? parentNode.getPropertyPath() : null)
				.withElement(sourceAccessor.getPathElement())
				.build();
	}
}
