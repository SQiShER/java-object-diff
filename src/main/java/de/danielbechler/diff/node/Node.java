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

/** @author Daniel Bechler */
public interface Node extends CanonicalAccessor
{
	public static final Node ROOT = null;

	public static interface Visitor
	{
		void accept(Node difference, Visit visit);
	}

	public enum State
	{
		ADDED,
		CHANGED,
		REMOVED,
		REPLACED,
		UNTOUCHED,
		IGNORED
	}

	Node getParentNode();

	void setParentNode(Node parent);

	State getState();

	void setState(State type);

	boolean matches(PropertyPath path);

	boolean isRootNode();

	boolean hasChanges();

	boolean isCollectionDifference();

	CollectionNode toCollectionDifference();

	boolean isMapDifference();

	MapNode toMapDifference();

	PropertyPath getPropertyPath();

	boolean hasChildren();

	Collection<Node> getChildren();

	Node getChild(String propertyName);

	Node getChild(PropertyPath absolutePath);

	Node getChild(PropertyPath.Element pathElement);

	void addChild(Node difference);

	void visit(Visitor visitor);

	void visitChildren(Visitor visitor);
}
