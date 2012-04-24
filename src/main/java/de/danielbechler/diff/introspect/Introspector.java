package de.danielbechler.diff.introspect;

import de.danielbechler.diff.accessor.*;

/** @author Daniel Bechler */
public interface Introspector
{
	Iterable<Accessor> introspect(Class<?> type);
}
