package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import de.danielbechler.util.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/** @author Daniel Bechler */
public class DefaultNode implements Node
{
	private final Accessor accessor;
	private final Map<PropertyPath.Element, Node> children = new LinkedHashMap<PropertyPath.Element, Node>(10);

	private State state = State.UNTOUCHED;
	private Node parentNode;

	public DefaultNode(final Node parentNode, final Accessor accessor)
	{
		Assert.notNull(accessor, "accessor");
		this.parentNode = parentNode;
		this.accessor = accessor;
	}

	public State getState()
	{
		return this.state;
	}

	public boolean matches(final PropertyPath path)
	{
		return path.matches(getPropertyPath());
	}

	public boolean hasChanges()
	{
		if (state != State.UNTOUCHED && state != State.IGNORED)
		{
			return true;
		}
		final AtomicBoolean result = new AtomicBoolean(false);
		visitChildren(new Visitor()
		{
			public void accept(final Node difference, final Visit visit)
			{
				if (difference.getState() != State.UNTOUCHED)
				{
					result.set(true);
					visit.stop();
				}
			}
		});
		return result.get();
	}

	public final PropertyPath getPropertyPath()
	{
		final PropertyPathBuilder builder = new PropertyPathBuilder();
		if (parentNode != null)
		{
			builder.withPropertyPath(parentNode.getPropertyPath());
		}
		builder.withElement(accessor.getPathElement());
		return builder.build();
	}

	public PropertyPath.Element getPathElement()
	{
		return accessor.getPathElement();
	}

	public boolean isCollectionDifference()
	{
		return false;
	}

	public CollectionNode toCollectionDifference()
	{
		throw new UnsupportedOperationException();
	}

	public boolean isMapDifference()
	{
		return false;
	}

	public MapNode toMapDifference()
	{
		throw new UnsupportedOperationException();
	}

	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	public Collection<Node> getChildren()
	{
		return children.values();
	}

	@SuppressWarnings({"UnusedDeclaration", "TypeMayBeWeakened"})
	public void setChildren(final Collection<Node> children)
	{
		this.children.clear();
		for (final Node child : children)
		{
			addChild(child);
		}
	}

	public Node getChild(final String propertyName)
	{
		return children.get(new NamedPropertyElement(propertyName));
	}

	public Node getChild(final PropertyPath absolutePath)
	{
		final PropertyVisitor visitor = new PropertyVisitor(absolutePath);
		visitChildren(visitor);
		return visitor.getNode();
	}

	public Node getChild(final PropertyPath.Element pathElement)
	{
		return children.get(pathElement);
	}

	public void addChild(final Node child)
	{
		child.setParentNode(this);
		children.put(child.getPathElement(), child);
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
		for (final Node child : getChildren())
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

	public final boolean isRootNode()
	{
		return parentNode == null;
	}

	public final boolean isEqualsOnly()
	{
		return accessor.isEqualsOnly();
	}

	public final boolean isIgnored()
	{
		return accessor.isIgnored();
	}

	public final Set<String> getCategories()
	{
		final Set<String> set = new TreeSet<String>();
		if (parentNode != null)
		{
			set.addAll(parentNode.getCategories());
		}
		if (accessor.getCategories() != null)
		{
			set.addAll(accessor.getCategories());
		}
		return set;
	}

	public final void setState(final State state)
	{
		this.state = state;
	}

	public Node getParentNode()
	{
		return parentNode;
	}

	public void setParentNode(final Node parentNode)
	{
		this.parentNode = parentNode;
	}

	public Object get(final Object target)
	{
		return accessor.get(target);
	}

	public void set(final Object target, final Object value)
	{
		accessor.set(target, value);
	}

	public void unset(final Object target)
	{
		accessor.unset(target);
	}

	public Object canonicalGet(Object target)
	{
		if (parentNode != null)
		{
			target = parentNode.canonicalGet(target);
		}
		return accessor.get(target);
	}

	public void canonicalSet(Object target, final Object value)
	{
		if (parentNode != null)
		{
			target = parentNode.canonicalGet(target);
		}
		accessor.set(target, value);
	}

	public void canonicalUnset(Object target)
	{
		if (parentNode != null)
		{
			target = parentNode.canonicalGet(target);
		}
		accessor.unset(target);
	}
}
