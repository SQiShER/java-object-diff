/*
 * Copyright 2013 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.example;

import de.danielbechler.diff.*;
import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
public class RuntimeTypeResolutionExample
{
	private RuntimeTypeResolutionExample()
	{
	}

	public static class Coordinate
	{
		private final long x;
		private final long y;

		public Coordinate(final long x, final long y)
		{
			this.x = x;
			this.y = y;
		}

		public long getX()
		{
			return x;
		}

		public long getY()
		{
			return y;
		}
	}

	public static class Coordinate3D extends Coordinate
	{
		private final long z;

		public Coordinate3D(final long x, final long y, final long z)
		{
			super(x, y);
			this.z = z;
		}

		public long getZ()
		{
			return z;
		}
	}

	public static class Point
	{
		private final Coordinate coordinate;

		public Point(final Coordinate coordinate)
		{
			this.coordinate = coordinate;
		}

		public Coordinate getCoordinate()
		{
			return coordinate;
		}
	}

	public static void main(final String[] args)
	{
		final Point base = new Point(new Coordinate3D(1, 2, 3));
		final Point working = new Point(new Coordinate3D(1, 2, 30));
		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		assert node.getChild("coordinate").getChild("z").isChanged() :
				"The changed 'z' coordinate should have been detected because the property type should be resolved to Coordinate3D at runtime.";
	}
}
