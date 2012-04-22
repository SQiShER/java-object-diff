package de.danielbechler.diff;

import org.junit.*;

/** @author Daniel Bechler */
public class Bean2DifferTest
{
	@Test
	public void testCompare() throws Exception
	{
		final BeanDiffer beanDiffer = new BeanDiffer();
		beanDiffer.compare("foo", "bar");
	}
}
