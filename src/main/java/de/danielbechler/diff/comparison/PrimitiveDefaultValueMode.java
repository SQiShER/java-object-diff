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

package de.danielbechler.diff.comparison;

/**
 * Defines how default values of primitive types (int, long, short, byte, char, boolean, float, double) will
 * be treated. A default value is either the one specified by the JDK (numbers are 0, booleans are false) or
 * the value of the corresponding property when a new instance of its holding class gets created. In order to
 * determine the proper default value, we'll attempt to instantiate the holding class once via its public
 * constructor. If this instantiation fails (for example if there is no such constructor), we'll fall back to
 * the JDK default. This configuration does not apply to the corresponding wrapper types (Integer, Long,
 * Short, Byte, Character, Boolean, Float, Double).
 */
public enum PrimitiveDefaultValueMode
{
	/**
	 * Default values of primitive types will be treated like any other value. Since there is no distinction, any
	 * change to a primitive value will be marked as {@linkplain de.danielbechler.diff.node.DiffNode.State#CHANGED}.
	 */
	ASSIGNED,

	/**
	 * Default values of primitive types will be treated as if the property has not been set. The consequence of
	 * this is that a change from default value to something else will be marked as {@linkplain
	 * de.danielbechler.diff.node.DiffNode.State#ADDED} and from something else to the default value as {@linkplain
	 * de.danielbechler.diff.node.DiffNode.State#REMOVED}.
	 */
	UNASSIGNED
}
