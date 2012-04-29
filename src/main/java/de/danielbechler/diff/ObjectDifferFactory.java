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

package de.danielbechler.diff;

/**
 * Creates new {@link ObjectDiffer} instances.
 *
 * @author Daniel Bechler
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor"})
public final class ObjectDifferFactory
{
	ObjectDifferFactory()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a new {@link ObjectDiffer} instance.
	 *
	 * @return A new {@link ObjectDiffer} instance.
	 */
	public static ObjectDiffer getInstance()
	{
		return new DelegatingObjectDifferImpl();
	}

	/**
	 * Returns a new {@link ObjectDiffer} instance and passes it the given {@link Configuration}
	 *
	 * @param configuration The configuration for the {@link ObjectDiffer}.
	 *
	 * @return A new {@link ObjectDiffer} instance.
	 */
	public static ObjectDiffer getInstance(final Configuration configuration)
	{
		final DelegatingObjectDifferImpl objectDiffer = new DelegatingObjectDifferImpl();
		objectDiffer.setConfiguration(configuration);
		return objectDiffer;
	}
}
