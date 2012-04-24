package de.danielbechler.diff;

import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
public interface ObjectDiffer extends Differ, Configurable
{
	<T> Node compare(T working, T base);

	boolean isIgnored(Node parentNode, final Instances instances);

	boolean isEqualsOnly(Node parentNode, Instances instances);
}
