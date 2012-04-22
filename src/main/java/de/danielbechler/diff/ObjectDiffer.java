package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
public interface ObjectDiffer
{
	<T> DiffNode<T> compare(T modifiedInstance, T baseInstance);

	DiffNode compare(Object modifiedInstance, Object baseInstance, Object defaultInstance, Accessor accessor);
}
