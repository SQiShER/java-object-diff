/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;

import java.util.*;

/**
 * Represents a part of an object. It could be the object itself, one of its properties, an item in a collection or a map
 * entry. A node may one parent node and any number of children. It also provides methods to read and write the property
 * represented by this node on any object of the same type as the original object. Last but not least, a node knows how the
 * associated property has changed compared to the base object.
 *
 * @author Daniel Bechler
 */
@SuppressWarnings({"UnusedDeclaration"})
public interface Node extends CanonicalAccessor
{
	public static final Node ROOT = null;

	/** Visitor to traverse a node graph. */
	public static interface Visitor
	{
		void accept(Node node, Visit visit);
	}

	/** The state of a {@link Node} representing the difference between two objects. */
	public enum State
	{
		/** The value has been added to the working object. */
		ADDED,

		/** The value has been changed compared to the base object. */
		CHANGED,

		/** The value has been removed from the working object. */
		REMOVED,

		@Deprecated REPLACED,

		/** The value is identical between working and base */
		UNTOUCHED,

		/** The value has not been looked at and has been ignored. */
		IGNORED
	}

	/** @return The parent node, if any. */
	Node getParentNode();

	/**
	 * Sets the parent node.
	 *
	 * @param parent The parent of this node. May be null, if this is a root node.
	 */
	void setParentNode(Node parent);

	/** @return The state of this node. */
	State getState();

	/** @param state The state of this node. */
	void setState(State state);

	boolean matches(PropertyPath path);

	boolean isRootNode();

	boolean hasChanges();

	boolean isCollectionDifference();

	CollectionNode toCollectionDifference();

	boolean isMapDifference();

	MapNode toMapDifference();

	/** @return The absolute property path from the object root up to this node. */
	PropertyPath getPropertyPath();

	/** @return <code>true</code> if this node has children. */
	boolean hasChildren();

	/** @return The child nodes of this node. */
	Collection<Node> getChildren();

	/**
	 * Retrieve a child with the given property name relative to this node.
	 *
	 * @param propertyName The name of the property represented by the child node.
	 *
	 * @return The requested child node or <code>null</code>.
	 */
	Node getChild(String propertyName);

	/**
	 * Retrieve a child that matches the given absolute path.
	 *
	 * @param absolutePath The path from the object root to the requested child node.
	 *
	 * @return The requested child node or <code>null</code>.
	 */
	Node getChild(PropertyPath absolutePath);

	/**
	 * Retrieve a child that matches the given path element relative to this node.
	 *
	 * @param pathElement The path element of the childe node to get.
	 *
	 * @return The requested child node or <code>null</code>.
	 */
	Node getChild(PropertyPath.Element pathElement);

	/**
	 * Adds a child to this node and sets this node as its parent node.
	 *
	 * @param node The node to add.
	 */
	void addChild(Node node);

	/**
	 * Visit this and all child nodes.
	 *
	 * @param visitor The visitor to use.
	 */
	void visit(Visitor visitor);

	/**
	 * Visit all child nodes but not this one.
	 *
	 * @param visitor The visitor to use.
	 */
	void visitChildren(Visitor visitor);
}
