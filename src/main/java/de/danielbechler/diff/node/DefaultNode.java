package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/** @author Daniel Bechler */
public class DefaultNode<T> implements DiffNode<T>
{
	private final Accessor<?> accessor;
	private final Map<String, DiffNode<?>> children = new LinkedHashMap<String, DiffNode<?>>(10);

	private DifferenceType type = DifferenceType.UNTOUCHED;
	private DiffNode<?> parent;

	public DefaultNode(final Accessor<?> accessor)
	{
		this.accessor = accessor;
	}

	public DifferenceType getType()
	{
		return this.type;
	}

	@SuppressWarnings({"unchecked"})
	public Accessor<T> getAccessor()
	{
		return (Accessor<T>) this.accessor;
	}

	public Accessor<T> getCanonicalAccessor()
	{
		if (hasParent())
		{
			final Accessor<?> parentAccessor = getParent().getCanonicalAccessor();
//			final Accessor<T> childAccessor = getAccessor();
			final Accessor<T> childAccessor = unchain(getAccessor());
			return new ChainedAccessor<T>(parentAccessor, childAccessor);
		}
		return getAccessor();
	}

	private Accessor<T> unchain(final Accessor<T> accessor)
	{
		if (accessor instanceof ChainingAccessor)
		{
			return unchain(((ChainingAccessor<T>) accessor).getDetachedChildAccessor());
		}
		return accessor;
	}

	public boolean matches(final PropertyPath path)
	{
		return path.matches(getPropertyPath());
	}

	public boolean isDifferent()
	{
		if (type != DifferenceType.UNTOUCHED)
		{
			return true;
		}
		final AtomicBoolean result = new AtomicBoolean(false);
		visitChildren(new Visitor()
		{
			public void accept(final DiffNode<?> difference, final Visit visit)
			{
				if (difference.getType() != DifferenceType.UNTOUCHED)
				{
					result.set(true);
					visit.stop();
				}
			}
		});
		return result.get();
	}

	public PropertyPath getPropertyPath()
	{
		final PropertyPath.Element pathElement = getAccessor().toPathElement();
		if (hasParent())
		{
			return new PropertyPath(parent.getPropertyPath(), pathElement);
		}
		return new PropertyPath(pathElement);
	}

	private boolean hasParent()
	{
		return parent != null;
	}

	public boolean isCollectionDifference()
	{
		return false;
	}

	public CollectionNode<?> toCollectionDifference()
	{
		throw new UnsupportedOperationException();
	}

	public boolean isMapDifference()
	{
		return false;
	}

	public MapNode<?, ?> toMapDifference()
	{
		throw new UnsupportedOperationException();
	}

	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	public Collection<DiffNode<?>> getChildren()
	{
		return children.values();
	}

	public void setChildren(final Collection<DiffNode<?>> differences)
	{
		children.clear();
		for (final DiffNode<?> difference : differences)
		{
			addChild(difference);
		}
	}

	public DiffNode<?> getChild(final String name)
	{
		return children.get(name);
	}

	public DiffNode<?> getChild(final PropertyPath path)
	{
		final ChildVisitor visitor = new ChildVisitor(path);
		visitChildren(visitor);
		return visitor.getDifference();
	}

	public void addChild(final DiffNode<?> difference)
	{
		difference.setParent(this);
		children.put(difference.getPropertyName(), difference);
	}

	public final void visit(final Visitor visitor)
	{
		final Visit visit = new Visit();
		try
		{
			visit(visitor, visit);
		}
		catch (final StopVisitationException ignored)
		{
		}
	}

	protected final void visit(final Visitor visitor, final Visit visit)
	{
		try
		{
			visitor.accept(this, visit);
		}
		catch (final StopVisitationException e)
		{
			visit.stop();
		}
		if (visit.isAllowedToGoDeeper() && hasChildren())
		{
			visitChildren(visitor);
		}
		if (visit.isStopped())
		{
			throw new StopVisitationException();
		}
	}

	public final void visitChildren(final Visitor visitor)
	{
		for (final DiffNode<?> child : getChildren())
		{
			try
			{
				child.visit(visitor);
			}
			catch (StopVisitationException e)
			{
				return;
			}
		}
	}

	public boolean isRoot()
	{
		return parent == null;
	}

	public Set<String> getCategories()
	{
		return getCanonicalAccessor().getCategories();
	}

	public void setType(final DifferenceType type)
	{
		this.type = type;
	}

	public String getPropertyName()
	{
		return accessor.getPropertyName();
	}

	public DiffNode<?> getParent()
	{
		return parent;
	}

	public void setParent(final DiffNode<?> parent)
	{
		this.parent = parent;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof DefaultNode))
		{
			return false;
		}

		final DiffNode that = (DiffNode) o;

		if (!getCanonicalAccessor().equals(that.getCanonicalAccessor()))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return getCanonicalAccessor().hashCode();
	}

//	@Override
//	public String toString()
//	{
//		final ToStringBuilder stringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
//		stringBuilder.append("type", getType());
////		stringBuilder.append("propertyPath", getPropertyPath());
//		stringBuilder.append("propertyName", getPropertyName());
//		if (hasChildren())
//		{
//			stringBuilder.append("children", getChildren());
//		}
//		return stringBuilder.toString();
//	}

}
