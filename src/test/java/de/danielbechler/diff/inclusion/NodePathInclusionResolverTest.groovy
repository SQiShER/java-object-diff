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

import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static de.danielbechler.diff.inclusion.Inclusion.*

@Subject(NodePathInclusionResolver)
class NodePathInclusionResolverTest extends Specification {

	NodePathInclusionResolver inclusionResolver = new NodePathInclusionResolver()

	def "GetInclusion: returns DEFAULT when no INCLUDE or EXCLUDE has been configured"() {
		given:
		  inclusionResolver.setInclusion(NodePath.with('foo'), configuredInclusion)
		expect:
		  inclusionResolver.getInclusion(Mock(DiffNode)) == DEFAULT
		where:
		  configuredInclusion << [null, DEFAULT]
	}

	@Unroll
	def "GetInclusion: returns #inclusion when the node itself has #inclusion inclusion"() {
		given:
		  def node = Stub(DiffNode) {
			  getPath() >> NodePath.with('foo')
		  }
		when:
		  inclusionResolver.setInclusion(node.path, inclusion)
		then:
		  inclusionResolver.getInclusion(node) == inclusion
		where:
		  inclusion << [INCLUDED, EXCLUDED]
	}

	@Unroll("GetInclusion: returns #expectedInclusion when the current node inclusion is #nodeInclusion and the closest parent inclusion is #parentInclusion")
	def "GetInclusion: determines inclusion by looking at the actual node and its closest parent with inclusion"() {
		given:
		  def parentNodePath = NodePath.with('foo')
		  def node = Stub(DiffNode) {
			  getPath() >> NodePath.startBuildingFrom(parentNodePath).propertyName('bar').build()
		  }
		when:
		  inclusionResolver.setInclusion(NodePath.with('any-other-node'), INCLUDED)
		  inclusionResolver.setInclusion(parentNodePath, parentInclusion)
		  inclusionResolver.setInclusion(node.path, nodeInclusion)
		then:
		  inclusionResolver.getInclusion(node) == expectedInclusion
		where:
		  nodeInclusion | parentInclusion || expectedInclusion
		  null          | null            || DEFAULT
		  null          | DEFAULT         || DEFAULT
		  null          | INCLUDED        || INCLUDED
		  null          | EXCLUDED        || EXCLUDED
		  DEFAULT       | null            || DEFAULT
		  DEFAULT       | DEFAULT         || DEFAULT
		  DEFAULT       | INCLUDED        || INCLUDED
		  DEFAULT       | EXCLUDED        || EXCLUDED
		  INCLUDED      | null            || INCLUDED
		  INCLUDED      | DEFAULT         || INCLUDED
		  INCLUDED      | INCLUDED        || INCLUDED
		  INCLUDED      | EXCLUDED        || EXCLUDED
		  EXCLUDED      | null            || EXCLUDED
		  EXCLUDED      | DEFAULT         || EXCLUDED
		  EXCLUDED      | INCLUDED        || EXCLUDED
		  EXCLUDED      | EXCLUDED        || EXCLUDED
	}

	def "GetInclusion: returns DEFAULT when neither the node itself nor its parents have an inclusion"() {
	}

	def "EnablesStrictIncludeMode: is true when at least one INCLUDE has been configured"() {
		expect:
		  !inclusionResolver.enablesStrictIncludeMode()

		when:
		  inclusionResolver.setInclusion(NodePath.with('foo'), INCLUDED)
		then:
		  inclusionResolver.enablesStrictIncludeMode()

		when:
		  inclusionResolver.setInclusion(NodePath.with('foo'), EXCLUDED)
		then:
		  !inclusionResolver.enablesStrictIncludeMode()
	}
}
