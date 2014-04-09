package de.danielbechler.diff.node;

import java.util.*;

/** @author Daniel Bechler */
public interface CategoryAwareAccessor extends Accessor
{
	Set<String> getCategories();
}
