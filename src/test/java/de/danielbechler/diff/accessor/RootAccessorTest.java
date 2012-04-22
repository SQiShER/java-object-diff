package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

/** @author Daniel Bechler */
public class RootAccessorTest
{
	private final Accessor<Object> accessor = new RootAccessor<Object>();

	@Test
	public void testGetPropertyName() throws Exception
	{
		Assert.assertThat(accessor.getPropertyName(), IsEqual.equalTo(""));
	}

	@Test
	public void testGetPath() throws Exception
	{
		Assert.assertThat(accessor.getPath(), IsEqual.equalTo(new PropertyPath(RootElement.getInstance())));
	}

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
		final Object replacement = new Object();
		accessor.unset(original, replacement);
	}

	@Test
	public void testToPathElement() throws Exception
	{
		Assert.assertThat(accessor.toPathElement(), Is.is(RootElement.class));
	}
}
