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
@SuppressWarnings({
		"UnusedDeclaration"
})
public class ObjectWithStringAndCompareTo implements Comparable<ObjectWithStringAndCompareTo>
{
	private String value;

	public ObjectWithStringAndCompareTo()
	{
	}

	public ObjectWithStringAndCompareTo(final String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}

    public int compareTo(ObjectWithStringAndCompareTo objectWithStringAndCompareTo) {
        return this.value.compareTo(objectWithStringAndCompareTo.value);
    }
}
