/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

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
