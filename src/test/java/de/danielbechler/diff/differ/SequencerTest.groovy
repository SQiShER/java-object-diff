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

import spock.lang.Specification

class SequencerTest extends Specification {

	def 'identical collections with single item'() {
		when:
		  def sequences = Sequencer.findSequences(['a'], ['a'])
		then:
		  sequences == [new Sequence(0, 0, 1)]
	}

	def test2() {
		when:
		  def sequences = Sequencer.findSequences(['a'], ['a', 'a'])
		then:
		  sequences == [new Sequence(0, 0, 1)]
	}

	def test3() {
		when:
		  def sequences = Sequencer.findSequences(
				  ['1', '2', '3', '4', '1', '3'],
				  ['1', '2', '1', '2', '3', '1']
		  )
		then:
		  sequences == [
				  new Sequence(0, 2, 3),
				  new Sequence(4, 0, 1)
		  ]
	}

	def test4() {
		when:
		  def sequences = Sequencer.findSequences(
				  ['1', '2', '3'],
				  ['2', '3']
		  )
		then:
		  sequences == [new Sequence(1, 0, 2)]
	}

	def test5() {
		when:
		  def sequences = Sequencer.findSequences(
				  ['1', '2'],
				  ['2', '1']
		  )
		then:
		  sequences == [
				  new Sequence(0, 1, 1),
				  new Sequence(1, 0, 1),
		  ]
	}

	def test6() {
		when:
		  def sequences = Sequencer.findSequences(
				  ['a', 'a'],
				  ['a']
		  )
		then:
		  sequences == [new Sequence(0, 0, 1)]
	}

}
