package de.danielbechler.diff.graph;

import java.util.*;

public class GraphNode
{
	private final int id;
	private final List<GraphNode> children = new LinkedList<GraphNode>();
	private final Map<String, Object> map = new HashMap<String, Object>(10);

	private GraphNode parent;
	private GraphNode directReference;
	private String value;

	public GraphNode()
	{
		this.id = -1;
	}

	/**
	 * @deprecated This constructor will implicitly add this node to the given parent node and should not be used
	 *             anymore.
	 */
	@Deprecated
	public GraphNode(final int id,
					 final GraphNode parent,
					 final String value)
	{
		this.id = id;
		this.parent = parent;
		this.parent.getChildren().add(this);
		this.value = value;
	}

	public GraphNode(final String value)
	{
		this.id = -1;
		this.value = value;
	}

	public GraphNode(final int id)
	{
		this.id = id;
	}

	public GraphNode(final int id, final String value)
	{
		this.id = id;
		this.value = value;
	}

	public void setDirectReference(final GraphNode directReference)
	{
		this.directReference = directReference;
	}

	public void addChild(final GraphNode child)
	{
		this.children.add(child);
	}

	public int getId()
	{
		return id;
	}

	public GraphNode getParent()
	{
		return parent;
	}

	public void setParent(final GraphNode parent)
	{
		this.parent = parent;
	}

	public String getValue()
	{
		return value;
	}

	public GraphNode getDirectReference()
	{
		return directReference;
	}

	public List<GraphNode> getChildren()
	{
		return children;
	}

	public Map<String, Object> getMap()
	{
		return map;
	}

	@Override
	public int hashCode()
	{
		return 1;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final GraphNode other = (GraphNode) obj;
		if (id != other.id)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("GraphNode");
		sb.append("{id=").append(id);
		sb.append(", value=").append(value);
		sb.append('}');
		return sb.toString();
	}
}
