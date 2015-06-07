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

package de.danielbechler.diff.differ

import de.danielbechler.diff.access.Instances
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.selector.ListItemSelector
import spock.lang.Specification
import spock.lang.Subject

import static de.danielbechler.diff.node.DiffNode.State.*

@Subject(ListDiffer)
class ListDifferTest extends Specification {

	def listDiffer = new ListDiffer()


	def 'addition'() {
		def working = ['added']
		def base = []

		when:
		  def node = listDiffer.compare(DiffNode.ROOT, Instances.of(working, base))

		then:
		  node.getChild(new ListItemSelector('added'))?.state == ADDED
	}

	def 'removal'() {
		def working = []
		def base = ['removed']

		when:
		  def node = listDiffer.compare(DiffNode.ROOT, Instances.of(working, base))

		then:
		  node.getChild(new ListItemSelector('removed'))?.state == REMOVED
	}

	def 'position change'() {
		def working = ['a', 'b']
		def base = working.reverse()

		when:
		  def node = listDiffer.compare(DiffNode.ROOT, Instances.of(working, base))

		then:
		  node.getChild(new ListItemSelector('a'))?.state == UNTOUCHED
		  node.getChild(new ListItemSelector('b'))?.state == UNTOUCHED
	}

	def 'addition of duplicate'() {
		def working = ['a', 'a']
		def base = ['a']

		when:
		  def node = listDiffer.compare(DiffNode.ROOT, Instances.of(working, base))

		then:
		  node.getChild(new ListItemSelector('a', 0))?.state == UNTOUCHED
		  node.getChild(new ListItemSelector('a', 1))?.state == ADDED
	}

	def 'removal of duplicate'() {
		def working = ['a']
		def base = ['a', 'a']

		when:
		  def node = listDiffer.compare(DiffNode.ROOT, Instances.of(working, base))

		then:
		  node.getChild(new ListItemSelector('a', 0))?.state == UNTOUCHED
		  node.getChild(new ListItemSelector('a', 1))?.state == REMOVED
	}
}
