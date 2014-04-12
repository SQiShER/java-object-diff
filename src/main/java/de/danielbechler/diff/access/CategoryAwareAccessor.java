package de.danielbechler.diff.access;

import java.util.Set;

/**
 * @author Daniel Bechler
 */
public interface CategoryAwareAccessor extends Accessor
{
	Set<String> getCategories();
}
