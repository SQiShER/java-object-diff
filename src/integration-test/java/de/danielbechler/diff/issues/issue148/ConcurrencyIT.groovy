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

package de.danielbechler.diff.issues.issue148

import de.danielbechler.diff.ObjectDiffer
import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ConcurrencyIT extends Specification {

	static class TestObject {
		def value
	}

	def test_concurrent() {
		ExecutorService executor = Executors.newFixedThreadPool(2)

		given: "one shared ObjectDiffer instance"
		  ObjectDiffer differ = ObjectDifferBuilder.buildDefault()

		when: "two Threads use the ObjectDiffer at the same time"
		  List<DiffNode> nodes = (0..1).collect({
			  return executor.submit(new Callable<DiffNode>() {
				  @Override
				  DiffNode call() throws Exception {
					  TestObject working = new TestObject(value: "")
					  TestObject base = new TestObject()
					  def node = differ.compare(working, base)
					  Thread.yield()
					  return node
				  }
			  })
		  }).collect({ Future<DiffNode> future -> future.get() })

		then:
		  noExceptionThrown()
		and:
		  nodes[0].hasChanges()
		and:
		  nodes[1].hasChanges()
	}
}
