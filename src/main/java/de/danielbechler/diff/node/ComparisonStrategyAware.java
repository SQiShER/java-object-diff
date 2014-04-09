package de.danielbechler.diff.node;

import de.danielbechler.diff.config.comparison.ComparisonStrategy;

/**
 * @author Daniel Bechler
 */
public interface ComparisonStrategyAware
{
	ComparisonStrategy getComparisonStrategy();
}
