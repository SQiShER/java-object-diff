package de.danielbechler.diff.config.circular;

/**
 * Allows to define how the circular reference detector compares object instances. By default it uses the equality
 * operator (`==`) which should be fine in mose cases.
 * <p/>
 * When dealing with object models that return copies of its properties on every access, it's possible to end up in
 * infinite loops, because even though the objects may look the same, they would be different instances. In those cases
 * it is possible to switch the instance detection mode to use the equals method instead of the equality operator. This
 * way objects will be considered to be "the same" whenever `equals` returns `true`.
 * <p/>
 * This configuration interface also allows to register a custom handler for exception thrown, whenever a circular
 * reference is detected. The default handler simply logs a warning.
 *
 * @author Daniel Bechler
 */
public interface CircularReferenceConfiguration
{
	CircularReferenceConfiguration matchCircularReferencesUsing(CircularReferenceMatchingMode matchingMode);

	CircularReferenceConfiguration handleCircularReferenceExceptionsUsing(CircularReferenceExceptionHandler exceptionHandler);
}
