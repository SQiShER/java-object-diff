/*
 * Copyright 2012 Daniel Bechler
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

package de.danielbechler.diff.mock;

/** @author Daniel Bechler */
public class ObjectWithPrimitiveProperties
{
	private char charValue;
	private byte byteValue;
	private short shortValue;
	private int intValue;
	private long longValue;
	private float floatValue;
	private double doubleValue;
	private boolean booleanValue;

	public char getCharValue()
	{
		return charValue;
	}

	public void setCharValue(final char charValue)
	{
		this.charValue = charValue;
	}

	public byte getByteValue()
	{
		return byteValue;
	}

	public void setByteValue(final byte byteValue)
	{
		this.byteValue = byteValue;
	}

	public short getShortValue()
	{
		return shortValue;
	}

	public void setShortValue(final short shortValue)
	{
		this.shortValue = shortValue;
	}

	public int getIntValue()
	{
		return intValue;
	}

	public void setIntValue(final int intValue)
	{
		this.intValue = intValue;
	}

	public long getLongValue()
	{
		return longValue;
	}

	public void setLongValue(final long longValue)
	{
		this.longValue = longValue;
	}

	public float getFloatValue()
	{
		return floatValue;
	}

	public void setFloatValue(final float floatValue)
	{
		this.floatValue = floatValue;
	}

	public double getDoubleValue()
	{
		return doubleValue;
	}

	public void setDoubleValue(final double doubleValue)
	{
		this.doubleValue = doubleValue;
	}

	public boolean isBooleanValue()
	{
		return booleanValue;
	}

	public void setBooleanValue(final boolean booleanValue)
	{
		this.booleanValue = booleanValue;
	}
}
