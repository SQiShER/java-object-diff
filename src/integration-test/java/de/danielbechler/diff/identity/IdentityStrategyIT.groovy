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

import de.danielbechler.diff.ObjectDiffer
import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.comparison.IdentityStrategy
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.CollectionItemElementSelector
import de.danielbechler.diff.selector.ElementSelector
import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import spock.lang.Specification

class IdentityStrategyIT extends Specification {

	def '#scenario IdentityStrategy to determine item identity'() {
		def ObjectDifferBuilder objectDifferBuilder = ObjectDifferBuilder.startBuilding()
		def ObjectDiffer objectDiffer
		def ElementSelector baseSelector
		def ElementSelector workingSelector

		given:
		  def baseCode = new Code(id: "foo", code: "original code")
		  def workingCode = new Code(id: "foo", code: "modified code")

		expect:
		  if (identityStrategyEnabled) {
			  def identityStrategy = new CodeIdentityStrategy()
			  objectDiffer = objectDifferBuilder.comparison()
					  .ofCollectionItems(NodePath.withRoot())
					  .toUse(identityStrategy)
					  .and().build()
			  baseSelector = new CollectionItemElementSelector(baseCode.clone())
			  workingSelector = new CollectionItemElementSelector(workingCode.clone())
		  } else {
			  objectDiffer = objectDifferBuilder.build()
			  baseSelector = new CollectionItemElementSelector(baseCode.clone())
			  workingSelector = new CollectionItemElementSelector(workingCode.clone())
		  }
		  def node = objectDiffer.compare([workingCode], [baseCode])
		  node.getChild(baseSelector).state == expectedBaseState
		  node.getChild(workingSelector).state == expectedWorkingState

		where:
		  identityStrategyEnabled || expectedBaseState 		| expectedWorkingState
		  false                   || DiffNode.State.CHANGED | DiffNode.State.CHANGED
		  true                    || DiffNode.State.REMOVED | DiffNode.State.ADDED

		  scenario = identityStrategyEnabled ? 'should use' : 'should not use'
	}

	@AutoClone
	@EqualsAndHashCode(includes = ['id'])
	@ToString(includePackage = false)
	public static class Code {
		String id
		String code
	}

	public static class CodeIdentityStrategy implements IdentityStrategy {
		@Override
		boolean equals(final Object working, final Object base) {
			return Objects.equals(((Code) working).getCode(), ((Code) base).getCode())
		}
	}
}
