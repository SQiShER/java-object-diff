package de.danielbechler.diff;

import org.hamcrest.core.*;
import org.junit.*;

/** @author Daniel Bechler */
public class ObjectDifferFactoryTest
{
	@Test(expected = IllegalArgumentException.class)
	public void testGetInstanceWithNullConfiguration() throws Exception
	{
		ObjectDifferFactory.getInstance(null);
	}

	@Test
	public void testGetInstanceWithConfiguration() throws Exception
	{
		final Configuration configuration = new Configuration();
		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance(configuration);
		Assert.assertThat(objectDiffer.getConfiguration(), IsEqual.equalTo(configuration));
	}

	@Test
	public void testGetInstance() throws Exception
	{
		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		Assert.assertThat(objectDiffer, IsNull.notNullValue());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testConstruction()
	{
		new ObjectDifferFactory();
	}
}
