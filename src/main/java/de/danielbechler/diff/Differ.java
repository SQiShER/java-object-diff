package de.danielbechler.diff;

import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
public interface Differ
{
	Node compare(Node parentNode, Instances instances);
}
