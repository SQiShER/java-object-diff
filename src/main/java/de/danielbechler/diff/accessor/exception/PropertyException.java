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

package de.danielbechler.diff.accessor.exception;

/** @author Daniel Bechler */
public class PropertyException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private String propertyName;
	private Class<?> targetType;

	public PropertyException(final Throwable cause)
	{
		super(cause);
	}

	@Override
	public String getMessage()
	{
		return String.format("Property '%s' on target of type %s.", propertyName, targetType);
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public void setPropertyName(final String propertyName)
	{
		this.propertyName = propertyName;
	}

	public Class<?> getTargetType()
	{
		return targetType;
	}

	public void setTargetType(final Class<?> targetType)
	{
		this.targetType = targetType;
	}
}
