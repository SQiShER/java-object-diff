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

package de.danielbechler.diff.instantiation;

public class TypeInstantiationException extends RuntimeException
{
	private static final long serialVersionUID = 2794794949615814237L;
	private final Class<?> type;
	private final String reason;

	public TypeInstantiationException(final Class<?> type, final String reason, final Throwable cause)
	{
		super("Failed to create instance of type '" + type + "'. Reason: " + reason, cause);
		this.type = type;
		this.reason = reason;
	}

	public Class<?> getType()
	{
		return type;
	}

	public String getReason()
	{
		return reason;
	}
}
