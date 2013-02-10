/*
 * Copyright 2012 Daniel Bechler
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

package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import de.danielbechler.util.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.Collections;
import java.util.concurrent.atomic.*;

import static java.util.Collections.*;

/** @author Daniel Bechler */
public class DefaultNode implements Node
{
	private final Accessor accessor;
	private final Map<Element, Node> children = new LinkedHashMap<Element, Node>(10);

	private State state = State.UNTOUCHED;
	private Node parentNode;
	private PropertyPath circleStartPath;
	private Node circleStartNode;
	private Class<?> valueType;

	public DefaultNode(final Node parentNode, final Accessor accessor, final Class<?> valueType)
	{
		Assert.notNull(accessor, "accessor");
		this.accessor = accessor;
		this.valueType = valueType;
		setParentNode(parentNode);
	}

	public DefaultNode(final Accessor accessor, final Class<?> valueType)
	{
		this(Node.ROOT, accessor, valueType);
	}

	public DefaultNode(final Class<?> valueType)
	{
		this(Node.ROOT, RootAccessor.getInstance(), valueType);
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
		if (isAdded() || isChanged() || isRemoved())
		{
			return true;
		}
		final AtomicBoolean result = new AtomicBoolean(false);
		visitChildren(new Visitor()
		{
			public void accept(final Node node, final Visit visit)
			{
				if (node.hasChanges())
				{
					result.set(true);
					visit.stop();
				}
			}
		});
		return result.get();
	}

	public final boolean isAdded()
	{
		return state == State.ADDED;
	}

	public final boolean isChanged()
	{
		return state == State.CHANGED;
	}

	public final boolean isRemoved()
	{
		return state == State.REMOVED;
	}

	public final boolean isUntouched()
	{
		return state == State.UNTOUCHED;
	}

	public boolean isCircular()
	{
		return state == State.CIRCULAR;
	}

	public final PropertyPath getPropertyPath()
	{
		if (parentNode != null)
		{
			return PropertyPath.createBuilder()
							   .withPropertyPath(parentNode.getPropertyPath())
							   .withElement(accessor.getPathElement())
							   .build();
		}
		else if (accessor instanceof RootAccessor)
		{
			return PropertyPath.createBuilder().withRoot().build();
		}
		else
		{
			return PropertyPath.createBuilder().withRoot().withElement(accessor.getPathElement()).build();
		}
	}

	public Element getPathElement()
	{
		return accessor.getPathElement();
	}

	public boolean isCollectionNode()
	{
		return false;
	}

	public CollectionNode toCollectionNode()
	{
		throw new UnsupportedOperationException();
	}

	public boolean isMapNode()
	{
		return false;
	}

	public MapNode toMapNode()
	{
		throw new UnsupportedOperationException();
	}

	public Class<?> getType()
	{
		if (accessor instanceof TypeAwareAccessor)
		{
			return ((TypeAwareAccessor) accessor).getType();
		}
		return valueType;
	}

	public void setType(final Class<?> aClass)
	{
		this.valueType = aClass;
	}

	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	public Collection<Node> getChildren()
	{
		return children.values();
	}

	public Node getChild(final String propertyName)
	{
		return children.get(new NamedPropertyElement(propertyName));
	}

	public Node getChild(final PropertyPath path)
	{
		final PropertyVisitor visitor = new PropertyVisitor(path);
		visitChildren(visitor);
		return visitor.getNode();
	}

	public Node getChild(final Element pathElement)
	{
		return children.get(pathElement);
	}

	public boolean addChild(final Node node)
	{
		if (node.isRootNode())
		{
			throw new IllegalArgumentException("Detected attempt to add root node as child. " +
					"This is not allowed and must be a mistake.");
		}
		else if (node == this)
		{
			throw new IllegalArgumentException("Detected attempt to add a node to itself. " +
					"This would cause inifite loops and must never happen.");
		}
		else if (node.getParentNode() != null && node.getParentNode() != this)
		{
			throw new IllegalArgumentException("Detected attempt to add child node that is already the " +
					"child of another node. Adding nodes multiple times is not allowed, since it could " +
					"cause infinite loops.");
		}
		final Element pathElement = node.getPathElement();
		if (node.getParentNode() == null)
		{
			node.setParentNode(this);
			children.put(pathElement, node);
		}
		else if (node.getParentNode() == this)
		{
			children.put(pathElement, node);
		}
		else
		{
			throw new IllegalStateException("Detected attempt to replace the parent node of node at path '" + getPropertyPath() + "'");
		}
		if (state == State.UNTOUCHED && node.hasChanges())
		{
			state = State.CHANGED;
		}
		return true;
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

	public Set<Annotation> getPropertyAnnotations()
	{
		if (accessor instanceof PropertyAccessor)
		{
			return unmodifiableSet(((PropertyAccessor) accessor).getReadMethodAnnotations());
		}
		return unmodifiableSet(Collections.<Annotation>emptySet());
	}

	public <T extends Annotation> T getPropertyAnnotation(final Class<T> annotationClass)
	{
		if (accessor instanceof PropertyAccessor)
		{
			return ((PropertyAccessor) accessor).getReadMethodAnnotation(annotationClass);
		}
		return null;
	}

	public final boolean isRootNode()
	{
		return accessor instanceof RootAccessor;
	}

	public final boolean isEqualsOnly()
	{
		return accessor.isEqualsOnly();
	}

	public final boolean isIgnored()
	{
		return state == State.IGNORED || accessor.isIgnored();
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

	public void setState(final State state)
	{
		Assert.notNull(state, "state");
		this.state = state;
	}

	public Node getParentNode()
	{
		return parentNode;
	}

	public final void setParentNode(final Node parentNode)
	{
		if (this.parentNode != null && this.parentNode != parentNode)
		{
			throw new IllegalStateException("The parent of a node cannot be changed, once it's set.");
		}
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

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("(");
		sb.append("state=");
		sb.append(getState().toString());
		if (getType() != null)
		{
			sb.append(", type=").append(getType().getCanonicalName());
		}
		if (getChildren().size() == 1)
		{
			sb.append(", ").append(getChildren().size()).append(" child");
		}
		else if (getChildren().size() > 1)
		{
			sb.append(", ").append(getChildren().size()).append(" children");
		}
		else
		{
			sb.append(", no children");
		}
		if (!getCategories().isEmpty())
		{
			sb.append(", categorized as ").append(getCategories());
		}
		sb.append(", accessed via ").append(accessor);
		sb.append(')');
		return sb.toString();
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final DefaultNode that = (DefaultNode) o;

		if (!accessor.equals(that.accessor))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return accessor.hashCode();
	}

	public PropertyPath getCircleStartPath()
	{
		return circleStartPath;
	}

	public void setCircleStartPath(final PropertyPath circularStartPath)
	{
		this.circleStartPath = circularStartPath;
	}

	public Node getCircleStartNode()
	{
		return circleStartNode;
	}

	public void setCircleStartNode(final Node circleStartNode)
	{
		this.circleStartNode = circleStartNode;
	}
}
