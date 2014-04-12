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

package de.danielbechler.diff.circular;

/**
 * Defines how the {@link CircularReferenceDetector} compares object instances. The
 * default is {@link CircularReferenceMatchingMode#EQUALITY_OPERATOR} and this should be sufficient in mose
 * cases. However, you may be dealing with an object model that returns copies of its properties, instead of
 * reusing the exact same instance. In this cases it would be easy to end up in infinite loops, as the default
 * circular reference detection would not be able to detect this. In those cases you should switch to the
 * {@link #EQUALS_METHOD} mode. The trade-off is, that this renders you unable to nest equal but different
 * objects.
 */
public enum CircularReferenceMatchingMode
{
	/**
	 * Compares objects using the <code>==</code> operator.
	 */
	EQUALITY_OPERATOR,

	/**
	 * Compares objects using {@linkplain Object#equals(Object)}.
	 */
	EQUALS_METHOD
}
