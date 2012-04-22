package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;

import java.util.*;

/** @author Daniel Bechler */
public interface Accessor<T>
{
	String getPropertyName();

	PropertyPath getPath();

	Set<String> getCategories();

	T get(Object target);

	void set(Object target, Object value);

	void unset(Object target, Object value);

	PropertyPath.Element toPathElement();
}
