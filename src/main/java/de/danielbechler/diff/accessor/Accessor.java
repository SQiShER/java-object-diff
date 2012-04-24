package de.danielbechler.diff.accessor;

/** @author Daniel Bechler */
public interface Accessor extends PropertyDescriptor
{
	Object get(Object target);

	void set(Object target, Object value);

	void unset(Object target);
}
