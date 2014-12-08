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

package de.danielbechler.diff.access;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Daniel Bechler
 */
public interface PropertyAwareAccessor extends TypeAwareAccessor, CategoryAware, ExclusionAware
{
	String getPropertyName();

	Set<Annotation> getReadMethodAnnotations();

	<T extends Annotation> T getReadMethodAnnotation(Class<T> annotationClass);

   <T extends Annotation> T getAnnotation(Class<T> annotationClass);

   int getFieldModifiers();
}
