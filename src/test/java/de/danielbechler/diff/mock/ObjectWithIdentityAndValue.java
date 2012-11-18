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

import static de.danielbechler.util.Assert.*;

/** @author Daniel Bechler */
public class ObjectWithIdentityAndValue
{
	private final String id;
	private String value;

	public ObjectWithIdentityAndValue(final String id)
	{
		this(id, null);
	}

	public ObjectWithIdentityAndValue(final String id, final String value)
	{
		hasText(id, "id");
		this.id = id;
		this.value = value;
	}

	public String getId()
	{
		return id;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof ObjectWithIdentityAndValue))
		{
			return false;
		}

		final ObjectWithIdentityAndValue that = (ObjectWithIdentityAndValue) o;

		if (!id.equals(that.id))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("ObjectWithIdentityAndValue");
		sb.append("{id='").append(id).append('\'');
		sb.append(", value='").append(value).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
