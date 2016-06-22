/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.access;

import de.danielbechler.util.Assert;
import de.danielbechler.util.Classes;
import de.danielbechler.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static de.danielbechler.util.Objects.isEqual;

public class Instances
{
	private static final Logger logger = LoggerFactory.getLogger(Instances.class);
	private final Accessor sourceAccessor;
	private final Object working;
	private final Object base;
	private final Object fresh;

	Instances(final Accessor sourceAccessor,
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

	public static <T> Instances of(final Accessor sourceAccessor,
								   final T working,
								   final T base,
								   final T fresh)
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
		return new Instances(RootAccessor.getInstance(), working, base, fresh);
	}

	/**
	 * @return The {@link Accessor} that has been used to get to these instances.
	 */
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
		if (fresh == null)
		{
			if (isPrimitiveNumericType())
			{
				return 0;
			}
			else if (isPrimitiveBooleanType())
			{
				return false;
			}
		}
		return fresh;
	}

	@SuppressWarnings("UnusedDeclaration")
	public <T> T getFresh(final Class<T> type)
	{
		final Object o = getFresh();
		return o != null ? type.cast(o) : null;
	}

	public boolean hasBeenAdded()
	{
		if (working != null && base == null)
		{
			return true;
		}
		if (isPrimitiveType() && isEqual(getFresh(), base) && !isEqual(base, working))
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
		if (isPrimitiveType() && isEqual(getFresh(), working) && !isEqual(base, working))
		{
			return true;
		}
		return false;
	}

	public boolean isPrimitiveType()
	{
		return Classes.isPrimitiveType(getType());
	}

	@SuppressWarnings("UnusedDeclaration")
	public boolean isPrimitiveWrapperType()
	{
		return Classes.isPrimitiveWrapperType(getType());
	}

	public Class<?> getType()
	{
		final Set<Class<?>> types = Classes.typesOf(working, base, fresh);
		final Class<?> sourceAccessorType = tryToGetTypeFromSourceAccessor();
		if (Classes.isPrimitiveType(sourceAccessorType))
		{
			return sourceAccessorType;
		}
		if (types.isEmpty())
		{
			return null;
		}
		if (types.size() == 1)
		{
			return Collections.firstElementOf(types);
		}
		if (types.size() > 1)
		{
// 			The following lines could be added if more precise type resolution is required:
//
//			if (Classes.allAssignableFrom(SortedSet.class, types))
//			{
//				return SortedSet.class;
//			}
//			if (Classes.allAssignableFrom(Set.class, types))
//			{
//				return Set.class;
//			}
//			if (Classes.allAssignableFrom(Queue.class, types))
//			{
//				return Queue.class;
//			}
//			if (Classes.allAssignableFrom(Deque.class, types))
//			{
//				return Deque.class;
//			}
//			if (Classes.allAssignableFrom(List.class, types))
//			{
//				return List.class;
//			}
//			else if (Classes.allAssignableFrom(SortedMap.class, types))
//			{
//				return SortedMap.class;
//			}
			if (Classes.allAssignableFrom(Collection.class, types))
			{
				return Collection.class;
			}
			else if (Classes.allAssignableFrom(Map.class, types))
			{
				return Map.class;
			}
			else
			{
				final Class<?> sharedType = Classes.mostSpecificSharedType(types);
				if (sharedType != null)
				{
					return sharedType;
				}
				else if (sourceAccessorType != null)
				{
					return sourceAccessorType;
				}
				// special handling for beans and arrays should go here
			}
		}

		logger.info("Detected instances of different types " + types + ". " +
			    "These objects will only be compared via equals method.");
		return Object.class;
	}

	private Class<?> tryToGetTypeFromSourceAccessor()
	{
		if (sourceAccessor instanceof TypeAwareAccessor)
		{
			return ((TypeAwareAccessor) sourceAccessor).getType();
		}
		return null;
	}

	public boolean isPrimitiveNumericType()
	{
		return Classes.isPrimitiveNumericType(getType());
	}

	private boolean isPrimitiveBooleanType()
	{
		return getType() == boolean.class;
	}

	public boolean areEqual()
	{
		return isEqual(base, working);
	}

	public boolean areSame()
	{
		return working == base;
	}

	public boolean areNull()
	{
		return working == null && base == null;
	}

}
