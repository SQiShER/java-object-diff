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

package de.danielbechler.diff.inclusion

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.node.DiffNode
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import spock.lang.Specification

class PropertyInclusionViaInclusionResolverAT extends Specification {

	def 'custom inclusion resolvers can be registered via config API'() {
		def base = [foo: 'original', bar: 'original'] as TestObject
		def working = [foo: 'changed', bar: 'changed'] as TestObject

		given: "custom inclusion resolver that excludes all properties named 'foo'"
		  def inclusionResolver = new InclusionResolver() {
			  @Override
			  boolean enablesStrictIncludeMode() {
				  return false
			  }

			  @Override
			  Inclusion getInclusion(DiffNode node) {
				  if (node.propertyAware && node.propertyName == 'foo') {
					  return Inclusion.EXCLUDED
				  }
				  return Inclusion.INCLUDED
			  }
		  }
		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .inclusion()
				  .resolveUsing(inclusionResolver)
				  .and()
				  .build()
				  .compare(working, base)
		then:
		  node.getChild('foo') == null
		  node.getChild('bar').changed
	}

	@ToString
	@EqualsAndHashCode
	class TestObject {
		def foo
		def bar
	}
}
