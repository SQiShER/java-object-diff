package de.danielbechler.diff;

/** @author Daniel Bechler */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor"})
public final class ObjectDifferFactory
{
	ObjectDifferFactory()
	{
		throw new UnsupportedOperationException();
	}

	public static ObjectDiffer getInstance()
	{
		return new DelegatingObjectDiffer();
	}

	public static ObjectDiffer getInstance(final Configuration configuration)
	{
		final DelegatingObjectDiffer objectDiffer = new DelegatingObjectDiffer();
		objectDiffer.setConfiguration(configuration);
		return objectDiffer;
	}
}
