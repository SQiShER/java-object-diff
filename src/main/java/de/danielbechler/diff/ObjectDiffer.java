package de.danielbechler.diff;

import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
public interface ObjectDiffer extends Differ, Configurable
{
	Node compare(Object working, Object base);

	boolean isIgnored(Node parentNode, final Instances instances);

	boolean isEqualsOnly(Node parentNode, Instances instances);
}
