/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.diff.identity

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.comparison.IdentityStrategy
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.CollectionItemElementSelector
import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import spock.lang.Specification

class IdentityStrategyIT extends Specification {

	Code code1 = new Code(id: "Id1", code: "Code1")
	Code code2 = new Code(id: "Id2", code: "Code2")
	Code code3 = new Code(id: "Id3", code: "Code3")

	def 'Test field CODE equals CHANGED'() {
		given:
		  def code2WithModifiedCode = new Code(id: code2.id, code: "newCode")
		  def code2WithModifiedId = new Code(id: "newId", code: code2.code)
		  def base = [code1, code2, code3]
		  def working = [code1.clone(), code2WithModifiedCode, code2WithModifiedId]
		when:
		  def codeStrategy = new CodeIdentity()
		  def diffNode = ObjectDifferBuilder.startBuilding()
				  .comparison().ofCollectionItems(NodePath.withRoot())
				  .toUse(codeStrategy).and()
				  .build().compare(working, base)
		then:
		  diffNode.getChild(new CollectionItemElementSelector(new Code(code: "Code1"), codeStrategy)) == null
		  diffNode.getChild(new CollectionItemElementSelector(new Code(code: "newCode"), codeStrategy)).added
		  diffNode.getChild(new CollectionItemElementSelector(new Code(code: "Code2"), codeStrategy)).changed
		  diffNode.getChild(new CollectionItemElementSelector(new Code(code: "Code3"), codeStrategy)).removed
	}

	def 'should detect addition via IdentityStrategy'() {
		given:
		  def codeStrategy = new CodeIdentity()
		  def baseCode = new Code(id: "foo", code: "original code")
		  def workingCode = new Code(id: "foo", code: "modified code")
		expect: 'without identity strategy'
		  ObjectDifferBuilder.startBuilding().build()
				  .compare([workingCode], [baseCode])
				  .getChild(new CollectionItemElementSelector(workingCode.clone())).changed
		and: 'with identity strategy'
		  ObjectDifferBuilder.startBuilding()
				  .comparison().ofCollectionItems(NodePath.withRoot()).toUse(codeStrategy).and().build()
				  .compare([workingCode], [baseCode])
				  .getChild(new CollectionItemElementSelector(workingCode.clone(), codeStrategy)).added
	}

	@AutoClone
	@EqualsAndHashCode(includes = ['id'])
	@ToString(includePackage = false)
	public static class Code {
		String id
		String code
	}

	public static class CodeIdentity implements IdentityStrategy {
		@Override
		boolean equals(final Object working, final Object base) {
			return Objects.equals(((Code) working).getCode(), ((Code) base).getCode())
		}
	}
}
