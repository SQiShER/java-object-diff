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

import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.introspection.ObjectDiffProperty
import de.danielbechler.diff.introspection.TypeInfo
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static de.danielbechler.diff.inclusion.Inclusion.*

/**
 * Created by Daniel Bechler.
 */
@Subject(TypePropertyAnnotationInclusionResolver)
class TypePropertyAnnotationInclusionResolverTest extends Specification {
	def inclusionResolver = new TypePropertyAnnotationInclusionResolver()
	def node = Stub DiffNode, {
		getPropertyAnnotation(ObjectDiffProperty) >> null
		getParentNode() >> Stub(DiffNode, {
			getValueTypeInfo() >> Stub(TypeInfo, {
				getAccessors() >> [
						Stub(PropertyAwareAccessor, { getReadMethodAnnotation(ObjectDiffProperty) >> null })
				]
			})
		})
	}

	def 'should return DEFAULT when node has no annotated inclusion and no siblings'() {
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
	}

	@Unroll
	def 'should return #expected when annotated inclusion is #inclusion'() {
		when:
		  def result = inclusionResolver.getInclusion(node)
		then:
		  node.getPropertyAnnotation(ObjectDiffProperty) >> Stub(ObjectDiffProperty, { inclusion() >> inclusion })
		and:
		  result == expected
		where:
		  inclusion || expected
		  null      || DEFAULT
		  DEFAULT   || DEFAULT
		  INCLUDED  || INCLUDED
		  EXCLUDED  || EXCLUDED
	}

	def 'should return EXCLUDED when excluded() is true'() {
		when:
		  def result = inclusionResolver.getInclusion(node)
		then:
		  node.getPropertyAnnotation(ObjectDiffProperty) >> Stub(ObjectDiffProperty, { excluded() >> true })
		and:
		  result == EXCLUDED
	}

	def 'should ignore excluded() = true when inclusion() is set to INCLUDED'() {
		when:
		  def result = inclusionResolver.getInclusion(node)
		then:
		  node.getPropertyAnnotation(ObjectDiffProperty) >> Stub(ObjectDiffProperty, {
			  excluded() >> true
			  inclusion() >> INCLUDED
		  })
		and:
		  result == INCLUDED
	}

	@Unroll
	def 'should honor excluded() = true when inclusion() is set to #inclusion'() {
		when:
		  def result = inclusionResolver.getInclusion(node)
		then:
		  node.getPropertyAnnotation(ObjectDiffProperty) >> Stub(ObjectDiffProperty, {
			  excluded() >> true
			  inclusion() >> inclusion
		  })
		and:
		  result == expected
		where:
		  inclusion || expected
		  null      || EXCLUDED
		  DEFAULT   || EXCLUDED
	}

	def 'should return EXCLUDED when node is not explicitly included but any of its siblings is'() {
		when:
		  def result = inclusionResolver.getInclusion(node)
		then:
		  node.parentNode.valueTypeInfo.accessors >> [propertyAccessorWithInclusion(INCLUDED)]
		and:
		  result == EXCLUDED
	}

	def 'should return DEFAULT when node is not explicitly included but any of its siblings is excluded'() {
		when:
		  def result = inclusionResolver.getInclusion(node)
		then:
		  node.parentNode.valueTypeInfo.accessors >> [propertyAccessorWithInclusion(EXCLUDED)]
		and:
		  result == DEFAULT
	}

	def 'should not throw an exception when no parent node exists'() {
		when:
		  inclusionResolver.getInclusion(node)
		then:
		  node.parentNode >> null
		and:
		  noExceptionThrown()
	}

	def 'should not throw an exception when parent node has no TypeInfo'() {
		when:
		  inclusionResolver.getInclusion(node)
		then:
		  node.parentNode.valueTypeInfo >> null
		and:
		  noExceptionThrown()
	}

	private propertyAccessorWithInclusion(Inclusion inclusionValue) {
		Stub(PropertyAwareAccessor, {
			getReadMethodAnnotation(ObjectDiffProperty) >> Stub(ObjectDiffProperty, {
				inclusion() >> inclusionValue
			})
		})
	}

}
