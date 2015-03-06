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

package de.danielbechler.diff.introspection

import de.danielbechler.diff.mock.ObjectWithMap
import spock.lang.Specification

import java.lang.reflect.Method

import static java.util.Collections.singletonMap

/**
 * @author Daniel Bechler
 */
public class PropertyAccessorMapTest extends Specification {
	PropertyAccessor accessor
	Map<String, String> targetMap
	ObjectWithMap target

	def setup() {
		targetMap = [:]
		target = new ObjectWithMap()
		target.setMap(targetMap)
		accessor = createMapPropertyAccessor(true)
	}

	def 'assign by replacing existing content if no write method is available'() {
		when:
		  accessor.set(target, singletonMap("foo", "bar"))
		then:
		  targetMap.get('foo') == 'bar'
	}

	def 'assign nothing if target map is immutable'() {
		given:
		  targetMap = Collections.emptyMap()
		  target.setMap(targetMap)
		when:
		  accessor.set(target, singletonMap("foo", "bar"))
		then:
		  targetMap.isEmpty()
	}

	def 'assign nothing if target map is null and no write method is available'() {
		given:
		  target.setMap(null)
		when:
		  accessor.set(target, singletonMap("foo", "bar"))
		then:
		  target.map == null
	}

	private static PropertyAccessor createMapPropertyAccessor(boolean readOnly) throws NoSuchMethodException {
		final Method readMethod = ObjectWithMap.class.getMethod("getMap")
		final Method writeMethod = readOnly ? null : ObjectWithMap.class.getMethod("setMap", Map.class)
		return new PropertyAccessor("map", null, readMethod, writeMethod)
	}
}
