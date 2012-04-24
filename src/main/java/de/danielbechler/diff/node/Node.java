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
		void accept(Node difference, final Visit visit);
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

	boolean isMapDifference();

	boolean isCollectionDifference();

	CollectionNode toCollectionDifference();

	MapNode toMapDifference();

	PropertyPath getPropertyPath();

	boolean hasChildren();

	Collection<Node> getChildren();

	Node getChild(String name);

	Node getChild(PropertyPath path);

	void addChild(Node difference);

	void visit(Visitor visitor);

	void visitChildren(Visitor visitor);
}
