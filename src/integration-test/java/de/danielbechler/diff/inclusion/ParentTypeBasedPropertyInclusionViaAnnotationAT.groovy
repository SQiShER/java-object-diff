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
import de.danielbechler.diff.introspection.ObjectDiffProperty
import spock.lang.Specification

import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED
import static de.danielbechler.diff.node.DiffNode.State.IGNORED

/**
 * Created by Daniel Bechler.
 */
class ParentTypeBasedPropertyInclusionViaAnnotationAT extends Specification {

	def "a property can be included via annotation"() {
		given:
		  def working = new ObjectWithInclusion(name: 'working')
		  def base = new ObjectWithInclusion(name: 'base')
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild('name').isChanged()
	}

	class ObjectWithInclusion {
		String name

		@ObjectDiffProperty(inclusion = INCLUDED)
		String getName() {
			return name
		}

		void setName(String name) {
			this.name = name
		}
	}

	def "a property can be excluded via annotation inclusion flag"() {
		given:
		  def working = new ObjectWithExclusionViaInclusion(name: 'working')
		  def base = new ObjectWithExclusionViaInclusion(name: 'base')
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.childCount() == 0
	}

	private class ObjectWithExclusionViaInclusion {
		String name

		@ObjectDiffProperty(inclusion = EXCLUDED)
		String getName() {
			return name
		}

		void setName(String name) {
			this.name = name
		}
	}

	def "a property can be excluded via annotation exclude flag"() {
		given:
		  def working = new ObjectWithExclusionViaExcludedFlag(name: 'working')
		  def base = new ObjectWithExclusionViaExcludedFlag(name: 'base')
		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .filtering().returnNodesWithState(IGNORED).and()
				  .build()
				  .compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild('name').isIgnored()
	}

	private class ObjectWithExclusionViaExcludedFlag {
		String name

		@ObjectDiffProperty(excluded = true)
		String getName() {
			return name
		}

		void setName(String name) {
			this.name = name
		}
	}

	def "when a property is included its siblings will be ignored unless they are explicitly included"() {
		given:
		  def working = new ObjectWithInclusionAndSibling(name: 'working', sibling: 'working')
		  def base = new ObjectWithInclusionAndSibling(name: 'base', sibling: 'base')
		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .filtering().returnNodesWithState(IGNORED).and()
				  .build()
				  .compare(working, base)
		then:
		  node.childCount() == 2
		  node.getChild('name').isChanged()
		  node.getChild('sibling').isIgnored()
	}

	private class ObjectWithInclusionAndSibling {
		def name
		def sibling

		@ObjectDiffProperty(inclusion = INCLUDED)
		def getName() {
			return name
		}

		void setName(def name) {
			this.name = name
		}
	}

	def "when a property is excluded it doesn't affect the inclusion of its siblings"() {
		given:
		  def working = new ObjectWithExclusionAndSibling(name: 'working', sibling: 'working')
		  def base = new ObjectWithExclusionAndSibling(name: 'base', sibling: 'base')
		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .filtering().returnNodesWithState(IGNORED).and()
				  .build()
				  .compare(working, base)
		then:
		  node.childCount() == 2
		  node.getChild('name').isIgnored()
		  node.getChild('sibling').isChanged()
	}

	private class ObjectWithExclusionAndSibling {
		def name
		def sibling

		@ObjectDiffProperty(inclusion = EXCLUDED)
		def getName() {
			return name
		}

		void setName(def name) {
			this.name = name
		}
	}

	def "when a property is excluded via 'excluded' flag and included via 'inclusion' at the same time, the inclusion flag always wins"() {
		given:
		  def working = new ObjectWithConflictingAnnotationFlags(name: 'changed')
		  def base = new ObjectWithConflictingAnnotationFlags(name: 'about-to-get-changed')
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild('name').isChanged()
	}

	private class ObjectWithConflictingAnnotationFlags {
		def name

		@ObjectDiffProperty(excluded = true, inclusion = INCLUDED)
		def getName() {
			return name
		}

		void setName(def name) {
			this.name = name
		}
	}
}
