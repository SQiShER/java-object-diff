package de.danielbechler.diff.accessor;

/** @author Daniel Bechler */
public interface CanonicalAccessor extends Accessor
{
	Object canonicalGet(Object target);

	void canonicalSet(Object target, Object value);

	void canonicalUnset(Object target);
}
