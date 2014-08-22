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

package de.danielbechler.diff.differ;

import de.danielbechler.diff.ObjectDifferBuilder;

/**
 * Created by Daniel Bechler.
 */
public interface DifferConfigurer
{
	/**
	 * Registers a new Differ to be used when comparing objects. Differs that have been registered later always win
	 * over earlier Differs. This way it is easily possible to override the default behavior, without actually removing
	 * the standard differs.
	 *
	 * @param differFactory Factory that creates a new Differ. Will be called exactly once and the resulting Differ
	 *                      will be pushed to the underlying {@link de.danielbechler.diff.differ.DifferProvider}.
	 * @return The {@link de.danielbechler.diff.ObjectDifferBuilder} for chaining.
	 */
	ObjectDifferBuilder register(DifferFactory differFactory);
}
