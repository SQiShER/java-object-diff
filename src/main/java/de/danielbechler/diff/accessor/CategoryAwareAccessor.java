package de.danielbechler.diff.accessor;

import java.util.*;

/** @author Daniel Bechler */
public interface CategoryAwareAccessor extends Accessor
{
	Set<String> getCategories();
}
