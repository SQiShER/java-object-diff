/*
 * Copyright 2016 Daniel Bechler
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

package de.danielbechler.diff.node

import spock.lang.Specification

class CategoryFilteringVisitorTest extends Specification {

	private interface FilterCallback {
		void accept()

		void dismiss()
	}

	def node = DiffNode.newRootNode()
	def filterCallback = Mock(FilterCallback)
	def visitor = new CategoryFilteringVisitor() {
		@Override
		protected void onAccept(DiffNode node, Visit visit) {
			super.onAccept(node, visit)
			filterCallback.accept()
		}

		@Override
		protected void onDismiss(DiffNode node, Visit visit) {
			super.onDismiss(node, visit)
			filterCallback.dismiss()
		}
	}

	def setup() {
		node.addCategories(['foo'])
	}

	def 'dismisses node if category is neither included nor excluded and includeAllNonExcluded is false'() {
		given:
		  visitor.includeAllNonExcluded(false)
		when:
		  visitor.node(node, new Visit())
		then:
		  1 * filterCallback.dismiss()
	}

	def 'accepts node if category is neither included nor excluded but includeAllNonExcluded is true'() {
		given:
		  visitor.includeAllNonExcluded(true)
		when:
		  visitor.node(node, new Visit())
		then:
		  1 * filterCallback.accept()
	}

	def 'dismisses node if category is excluded'() {
		given:
		  visitor.includeAllNonExcluded(false)
		  visitor.exclude('foo')
		when:
		  visitor.node(node, new Visit())
		then:
		  1 * filterCallback.dismiss()
	}

	def 'accepts node if category is included'() {
		given:
		  visitor.includeAllNonExcluded(false)
		  visitor.include('foo')
		when:
		  visitor.node(node, new Visit())
		then:
		  1 * filterCallback.accept()
	}

	def 'on dismiss sets dontGoDeeper flag of Visit'() {
		given:
		  def visit = new Visit()
		and:
		  visitor.includeAllNonExcluded(false)
		expect:
		  visit.allowedToGoDeeper
		when:
		  visitor.node(node, visit)
		then:
		  !visit.allowedToGoDeeper
	}

	def 'on accept adds node to matches'() {
		given:
		  visitor.includeAllNonExcluded(false)
		  visitor.include('foo')
		expect:
		  !visitor.matches.contains(node)
		when:
		  visitor.node(node, new Visit())
		then:
		  visitor.matches.contains(node)
	}
}
