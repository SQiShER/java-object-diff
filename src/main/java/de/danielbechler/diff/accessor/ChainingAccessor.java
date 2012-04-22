package de.danielbechler.diff.accessor;

/** @author Daniel Bechler */
public interface ChainingAccessor<T> extends Accessor<T>
{
	Accessor<?> getParentAccessor();

	Accessor<T> getDetachedChildAccessor();
}
