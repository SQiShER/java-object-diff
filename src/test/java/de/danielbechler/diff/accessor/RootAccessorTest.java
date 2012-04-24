package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

/** @author Daniel Bechler */
public class RootAccessorTest
{
	private final Accessor accessor = new RootAccessor();

	@Test
	public void testGet() throws Exception
	{
		final Object root = new Object();
		Assert.assertThat(accessor.get(root), IsSame.sameInstance(root));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSet() throws Exception
	{
		final Object original = new Object();
		final Object replacement = new Object();
		accessor.set(original, replacement);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnset() throws Exception
	{
		final Object original = new Object();
		accessor.unset(original);
	}

	@Test
	public void testToPathElement() throws Exception
	{
		Assert.assertThat(accessor.getPathElement(), Is.is(RootElement.class));
	}
}
