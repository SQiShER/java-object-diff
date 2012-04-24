package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;

import java.util.*;

/** @author Daniel Bechler */
public interface PropertyDescriptor
{
	PropertyPath.Element getPathElement();

	Set<String> getCategories();

	boolean isIgnored();

	boolean isEqualsOnly();
}
