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

import de.danielbechler.diff.ObjectDifferBuilder;

/**
 * Allows to define how the circular reference detector compares object instances. By default it uses the equality
 * operator (`==`) which should be fine in most cases.
 * <p>
 * When dealing with object models that return copies of its properties on every access, it's possible to end up in
 * infinite loops, because even though the objects may look the same, they would be different instances. In those cases
 * it is possible to switch the instance detection mode to use the equals method instead of the equality operator. This
 * way objects will be considered to be "the same" whenever `equals` returns `true`.
 * <p>
 * This configuration interface also allows to register a custom handler for exception thrown, whenever a circular
 * reference is detected. The default handler simply logs a warning.
 *
 * @author Daniel Bechler
 */
public interface CircularReferenceConfigurer
{
	CircularReferenceConfigurer matchCircularReferencesUsing(CircularReferenceMatchingMode matchingMode);

	CircularReferenceConfigurer handleCircularReferenceExceptionsUsing(CircularReferenceExceptionHandler exceptionHandler);

	ObjectDifferBuilder and();
}
