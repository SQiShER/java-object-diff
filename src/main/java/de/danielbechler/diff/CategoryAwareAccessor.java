package de.danielbechler.diff;

import java.util.*;

/** @author Daniel Bechler */
public interface CategoryAwareAccessor extends Accessor
{
	Set<String> getCategories();
}
