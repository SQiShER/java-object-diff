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

package de.danielbechler.diff.bean;

/** @author Daniel Bechler */
public class BeanPropertyWriteException extends BeanPropertyException
{
	private static final long serialVersionUID = 1L;
	private final Object newValue;

	public BeanPropertyWriteException(final Throwable cause, final Object newValue)
	{
		super(cause);
		this.newValue = newValue;
	}

	@Override
	public String getMessage()
	{
		return "Error while invoking write method. ";
	}

	public Object getNewValue()
	{
		return newValue;
	}
}
