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
public class ObjectWithCircularReference
{
	private String id;
	private ObjectWithCircularReference reference;

	public ObjectWithCircularReference(final String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public ObjectWithCircularReference getReference()
	{
		return reference;
	}

	public void setReference(final ObjectWithCircularReference reference)
	{
		this.reference = reference;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("ObjectWithCircularReference");
		sb.append("{id='").append(id).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
