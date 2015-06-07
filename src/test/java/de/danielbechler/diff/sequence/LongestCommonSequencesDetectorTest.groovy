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

package de.danielbechler.diff.sequence

import spock.lang.Specification
import spock.lang.Unroll

class LongestCommonSequencesDetectorTest extends Specification {

	@Unroll("extract proper sequences for scenario: #description")
	def 'extract proper sequences'() {
		when:
		  Collection<Sequence> sequences = LongestCommonSequencesDetector.findSequences(working, base)
		  Collection<Sequence> expectedSequences = sequenceTuples.collect { new Sequence(it[0], it[1], it[2]) }
		then:
		  sequences.size() == sequenceTuples.size()
		and:
		  sequences == expectedSequences
		where:
		  working              | base                      || sequenceTuples         || description
		  []                   | []                        || []                     || 'no common sequence (empty lists)'
		  ['A']                | []                        || []                     || 'no common sequence (empty base)'
		  []                   | ['A']                     || []                     || 'no common sequence (empty working)'
		  ['A']                | ['A']                     || [[0, 0, 1]]            || 'single common sequence (identical lists with one item)'
		  ['A', 'A']           | ['A']                     || [[0, 0, 1]]            || 'single common sequence (occuring twice in working)'
		  ['A']                | ['A', 'A']                || [[0, 0, 1]]            || 'single common sequence (occuring twice in base)'
		  ['A', 'B']           | ['A', 'B']                || [[0, 0, 2]]            || 'single common sequence (identical lists with multiple items)'
		  ['B', 'A', 'B']      | ['A', 'B']                || [[1, 0, 2]]            || 'single common sequence (different position in working and base)'
		  ['A', 'B', 'A', 'B'] | ['A', 'B']                || [[0, 0, 2]]            || 'single common sequence (occuring twice in working)'
		  ['A', 'B']           | ['A', 'B', 'A', 'B']      || [[0, 0, 2]]            || 'single common sequence (occuring twice in base)'
		  ['A', 'B', 'C']      | ['C']                     || [[2, 0, 1]]            || 'single common sequence (at end of base)'
		  ['C']                | ['A', 'B', 'C']           || [[0, 2, 1]]            || 'single common sequence (at end of working)'
		  ['A', 'B', 'C', 'D'] | ['A', 'B', 'E', 'C', 'D'] || [[0, 0, 2], [2, 3, 2]] || 'multiple common sequences (separated by additional item in base)'
		  ['A', 'B', 'C', 'D'] | ['C', 'D', 'A', 'B']      || [[0, 2, 2], [2, 0, 2]] || 'multiple common sequences (occuring in reverse order)'
	}
}
