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

import de.danielbechler.diff.node.DiffNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("UnusedDeclaration")
public class DefaultPropertyAccessExceptionHandler implements BeanPropertyAccessExceptionHandler
{
	private static final Logger logger = LoggerFactory.getLogger(DefaultPropertyAccessExceptionHandler.class);

	public DiffNode onPropertyWriteException(final BeanPropertyWriteException exception,
											 final DiffNode propertyNode)
	{
		final Object newValue = exception.getNewValue();
		final String propertyName = exception.getPropertyName();
		logger.info("Couldn't set new value '{}' for property '{}'", newValue, propertyName);
		throw exception;
	}

	public DiffNode onPropertyReadException(final BeanPropertyReadException exception,
											final DiffNode propertyNode)
	{
		throw exception;
	}
}
