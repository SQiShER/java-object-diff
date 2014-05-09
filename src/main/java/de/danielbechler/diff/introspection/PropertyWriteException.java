/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.introspection;

/**
 * @author Daniel Bechler
 */
public class PropertyWriteException extends PropertyAccessException
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("TransientFieldNotInitialized")
	private final transient Object newValue;

	public PropertyWriteException(final String propertyName, final Class<?> targetType, final Object newValue, final Throwable cause)
	{
		super(propertyName, targetType, cause);
		this.newValue = newValue;
	}

	@Override
	public String getMessage()
	{
		return String.format("Failed to write new value '%s' to property '%s' of type '%s'", newValue, getPropertyName(), getTargetType().getCanonicalName());
	}

	public Object getNewValue()
	{
		return newValue;
	}
}
