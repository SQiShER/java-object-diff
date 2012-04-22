package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;

import java.util.*;

/** @author Daniel Bechler */
public interface DiffNode<T>
{
	public static interface Visitor
	{
		void accept(DiffNode<?> difference, final Visit visit);
	}

	String getPropertyName();

	DiffNode<?> getParent();

	void setParent(DiffNode<?> parent);

	DifferenceType getType();

	void setType(DifferenceType type);

	Accessor<T> getAccessor();

	Accessor<T> getCanonicalAccessor();

	boolean matches(PropertyPath path);

	boolean isDifferent();

	PropertyPath getPropertyPath();

	boolean isCollectionDifference();

	CollectionNode<?> toCollectionDifference();

	boolean isMapDifference();

	MapNode<?, ?> toMapDifference();

	boolean hasChildren();

	Collection<DiffNode<?>> getChildren();

	DiffNode<?> getChild(String name);

	DiffNode<?> getChild(PropertyPath path);

	void addChild(DiffNode<?> difference);

	void visit(Visitor visitor);

	void visitChildren(Visitor visitor);

	boolean isRoot();

	Set<String> getCategories();
}
