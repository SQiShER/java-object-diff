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
import de.danielbechler.diff.category.CategoryResolver
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static de.danielbechler.diff.inclusion.Inclusion.*

@SuppressWarnings("GroovyAssignabilityCheck")
@Subject(InclusionService)
class InclusionServiceTest extends Specification {

	def categoryResolver = Stub CategoryResolver
	def rootConfiguration = Stub ObjectDifferBuilder
	def categoryInclusionResolver = Mock CategoryInclusionResolver
	def typePropertyConfigInclusionResolver = Mock TypePropertyConfigInclusionResolver
	def typeInclusionResolver = Mock TypeInclusionResolver
	def nodePathInclusionResolver = Mock NodePathInclusionResolver
	def propertyNameInclusionResolver = Mock PropertyNameInclusionResolver

	InclusionService inclusionService = new InclusionService(categoryResolver, rootConfiguration) {
		@Override
		def CategoryInclusionResolver newCategoryInclusionResolver() {
			return categoryInclusionResolver
		}

		@Override
		def TypePropertyConfigInclusionResolver newTypePropertyConfigInclusionResolver() {
			return typePropertyConfigInclusionResolver
		}

		@Override
		def TypeInclusionResolver newTypeInclusionResolver() {
			return typeInclusionResolver
		}

		@Override
		def NodePathInclusionResolver newNodePathInclusionResolver() {
			return nodePathInclusionResolver
		}

		@Override
		def PropertyNameInclusionResolver newPropertyNameInclusionResolver() {
			return propertyNameInclusionResolver
		}
	}

	def 'construction: should fail if categoryResolver is null'() {
		when:
		  new InclusionService(null, rootConfiguration);
		then:
		  thrown(IllegalArgumentException)
	}

	def 'construction: should fail if objectDifferBuilder is null'() {
		when:
		  new InclusionService(categoryResolver, null);
		then:
		  thrown(IllegalArgumentException)
	}

	def 'isIgnored: should never return true for the root node'() {
		given:
		  def rootNode = new DiffNode()
		and:
		  inclusionService.include().node(NodePath.withRoot())
		expect:
		  inclusionService.isIgnored(rootNode) == false
	}

	@Unroll
	def 'isIgnored: should return #expectIgnored for nodes with #inclusion inclusion if strict include mode is #strictIncludeModeState'() {
		given:
		  inclusionService.resolveUsing(Stub(InclusionResolver, {
			  getInclusion(_) >> inclusion
			  enablesStrictIncludeMode() >> strictMode
		  }))
		expect:
		  inclusionService.isIgnored(Mock(DiffNode)) == expectIgnored
		where:
		  inclusion | strictMode || expectIgnored
		  null      | true       || true
		  DEFAULT   | true       || true
		  INCLUDED  | true       || false
		  EXCLUDED  | true       || true
		  null      | false      || false
		  DEFAULT   | false      || false
		  INCLUDED  | false      || false
		  EXCLUDED  | false      || true

		  strictIncludeModeState = strictMode ? 'enabled' : 'disabled'
		  ignoreState = expectIgnored ? 'ignore' : 'not ignore'
	}

	@Unroll
	def '#name: #inclusionText creates and activates (exactly one) #resolverType'() {
		expect:
		  inclusionService.inclusionResolvers.findAll { resolverType.isInstance(it) }.size() == 0
		when:
		  stimulus.call(inclusionService)
		  stimulus.call(inclusionService)
		then:
		  inclusionService.inclusionResolvers.findAll { resolverType.isInstance(it) }.size() == 1
		where:
		  name                 | inclusion | resolverType                        | stimulus
		  'propertyName'       | INCLUDED  | PropertyNameInclusionResolver       | { InclusionService service -> service.include().propertyName('foo') }
		  'propertyName'       | EXCLUDED  | PropertyNameInclusionResolver       | { InclusionService service -> service.exclude().propertyName('foo') }
		  'propertyNameOfType' | INCLUDED  | TypePropertyConfigInclusionResolver | { InclusionService service -> service.include().propertyNameOfType(String, 'foo') }
		  'propertyNameOfType' | EXCLUDED  | TypePropertyConfigInclusionResolver | { InclusionService service -> service.exclude().propertyNameOfType(String, 'foo') }
		  'category'           | INCLUDED  | CategoryInclusionResolver           | { InclusionService service -> service.include().category('foo') }
		  'category'           | EXCLUDED  | CategoryInclusionResolver           | { InclusionService service -> service.exclude().category('foo') }
		  'node'               | INCLUDED  | NodePathInclusionResolver           | { InclusionService service -> service.include().node(NodePath.with('foo')) }
		  'node'               | EXCLUDED  | NodePathInclusionResolver           | { InclusionService service -> service.exclude().node(NodePath.with('foo')) }
		  'type'               | INCLUDED  | TypeInclusionResolver               | { InclusionService service -> service.include().type(Object) }
		  'type'               | EXCLUDED  | TypeInclusionResolver               | { InclusionService service -> service.exclude().type(Object) }
		  inclusionText = inclusion == INCLUDED ? 'including' : 'excluding'
	}

	@Unroll
	def 'type: #inclusionText delegates to TypeInclusionResolver'() {
		when:
		  performInclusion.call(inclusionService)
		then:
		  1 * typeInclusionResolver.setInclusion(type, inclusion)
		where:
		  inclusion | type   | performInclusion
		  INCLUDED  | String | { InclusionService service -> service.include().type(String) }
		  EXCLUDED  | Date   | { InclusionService service -> service.exclude().type(Date) }
		  inclusionText = inclusion == INCLUDED ? 'including' : 'excluding'
	}

	@Unroll
	def 'category: #inclusionText delegates to CategoryInclusionResolver'() {
		given:
		  def expectedCategory = 'some-category'
		when:
		  stimulus.call(inclusionService, expectedCategory)
		then:
		  1 * categoryInclusionResolver.setInclusion(expectedCategory, inclusion)
		where:
		  inclusion | stimulus
		  INCLUDED  | { InclusionService service, String category -> service.include().category(category) }
		  EXCLUDED  | { InclusionService service, String category -> service.exclude().category(category) }

		  inclusionText = inclusion == INCLUDED ? 'including' : 'excluding'
	}

	@Unroll
	def 'propertyName: #inclusionText delegates to PropertyNameInclusionResolver'() {
		when:
		  performInclusion.call(inclusionService, 'some-property-name')
		then:
		  1 * propertyNameInclusionResolver.setInclusion('some-property-name', inclusion)
		where:
		  inclusion | performInclusion
		  INCLUDED  | { InclusionService service, String name -> service.include().propertyName(name) }
		  EXCLUDED  | { InclusionService service, String name -> service.exclude().propertyName(name) }

		  inclusionText = inclusion == INCLUDED ? 'including' : 'excluding'
	}

	@Unroll
	def 'node: #inclusionText delegates to NodePathInclusionResolver'() {
		def nodePath = NodePath.with('foo', 'bar')
		when:
		  performInclusion.call(inclusionService, nodePath)
		then:
		  1 * nodePathInclusionResolver.setInclusion(nodePath, inclusion)
		where:
		  inclusion | performInclusion
		  INCLUDED  | { InclusionService service, NodePath path -> service.include().node(path) }
		  EXCLUDED  | { InclusionService service, NodePath path -> service.exclude().node(path) }

		  inclusionText = inclusion == INCLUDED ? 'including' : 'excluding'
	}

	@Unroll
	def 'propertyNameOfType: #inclusionMethodName delegates to TypePropertyConfigInclusionResolver'() {
		when:
		  inclusionService.invokeMethod(inclusionMethodName, null).propertyNameOfType(String, 'foo')
		then:
		  1 * typePropertyConfigInclusionResolver.setInclusion(String, 'foo', inclusion)
		where:
		  inclusion << [INCLUDED, EXCLUDED]
		  inclusionMethodName = inclusion == INCLUDED ? 'include' : 'exclude'
	}

	def 'construction order of inclusion resolvers should be reflected by the collection'() {
		given:
		  inclusionService = new InclusionService(categoryResolver, rootConfiguration)
		when:
		  inclusionService
				  .include().propertyNameOfType(String, 'foo').also()
				  .include().propertyName('foo').also()
				  .include().type(Date).also()
				  .include().category('foo').also()
				  .include().node(NodePath.with('foo'))
		then:
		  inclusionService.with {
			  inclusionResolvers.size() == 6
			  inclusionResolvers[0] instanceof TypePropertyAnnotationInclusionResolver
			  inclusionResolvers[1] instanceof TypePropertyConfigInclusionResolver
			  inclusionResolvers[2] instanceof PropertyNameInclusionResolver
			  inclusionResolvers[3] instanceof TypeInclusionResolver
			  inclusionResolvers[4] instanceof CategoryInclusionResolver
			  inclusionResolvers[5] instanceof NodePathInclusionResolver
		  }
	}
}
