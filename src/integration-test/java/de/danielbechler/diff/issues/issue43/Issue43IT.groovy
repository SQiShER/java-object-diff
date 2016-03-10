/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.issues.issue43

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.path.NodePath
import groovy.transform.EqualsAndHashCode
import spock.lang.Issue
import spock.lang.Specification

@Issue('https://github.com/SQiShER/java-object-diff/issues/43')
public class Issue43IT extends Specification {

	def objectDifferBuilder = ObjectDifferBuilder.startBuilding()

	def 'should diff things'() {
		given:
		  // @formatter:off
		  ['things', 'include'].collect({ property -> NodePath.with property }).each {
			  objectDifferBuilder
					  .comparison()
						  .ofNode(it).toUseEqualsMethod()
					  .and()
					  .inclusion()
						  .include().node(it)
		  }
		  // @formatter:on
		and:
		  def thingOne = new Thing('a', 'b')
		  def thingTwo = new Thing('aa', 'bb')
		and:
		  def first = new ThingHolder([thingOne] as Set, 'ignore', 'include')
		  def second = new ThingHolder([thingTwo] as Set, 'ignore this change', 'include')
		when:
		  def node = objectDifferBuilder.build().compare(first, second)
		then:
		  node.changed
	}

	@EqualsAndHashCode
	private static class Thing {
		final String a
		final String b

		Thing(String a, String b) {
			this.a = a
			this.b = b
		}
	}

	@EqualsAndHashCode
	private static class ThingHolder {
		final Set<Thing> things
		final String ignore
		final String include

		ThingHolder(Set<Thing> things, String ignore, String include) {
			this.things = things
			this.ignore = ignore
			this.include = include
		}
	}
}
