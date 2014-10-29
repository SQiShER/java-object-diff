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

package de.danielbechler.diff.differ

import de.danielbechler.diff.access.Accessor
import de.danielbechler.diff.access.Instances
import de.danielbechler.diff.access.RootAccessor
import de.danielbechler.diff.circular.CircularReferenceDetector
import de.danielbechler.diff.circular.CircularReferenceDetectorFactory
import de.danielbechler.diff.circular.CircularReferenceExceptionHandler
import de.danielbechler.diff.filtering.IsReturnableResolver
import de.danielbechler.diff.inclusion.IsIgnoredResolver
import de.danielbechler.diff.node.DiffNode
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

import static de.danielbechler.diff.circular.CircularReferenceDetector.ReferenceMatchingMode.EQUALITY_OPERATOR

/**
 * Created by Daniel Bechler.
 */
@Subject(DifferDispatcher)
@Ignore("Man, this class is a pain in the ass to test. Need to find a better way to do it.")
class DifferDispatcherTest extends Specification {

	def differ = Stub Differ
	def differProvider = Stub DifferProvider, {
		retrieveDifferForType(_ as Class<?>) >> differ
	}
	def circularReferenceDetectorFactory = Stub CircularReferenceDetectorFactory, {
		createCircularReferenceDetector() >> new CircularReferenceDetector(EQUALITY_OPERATOR)
	}
	def circularReferenceExceptionHandler = Stub(CircularReferenceExceptionHandler)
	def isIgnoredResolver = Stub IsIgnoredResolver, {
		isIgnored(_ as DiffNode) >> false
	}
	def isReturnableResolver = Stub IsReturnableResolver, {
		isReturnable(_ as DiffNode) >> true
	}
	def differDispatcher = new DifferDispatcher(
			differProvider,
			circularReferenceDetectorFactory,
			circularReferenceExceptionHandler,
			isIgnoredResolver,
			isReturnableResolver)

	def "when a circular reference is detected"() {
		given:
		  def accessor = Stub Accessor
		  def accessedInstances = Mock Instances, {
			  areNull() >> false
			  getBase() >> new Object()
			  getWorking() >> new Object()
		  }
		  def instances = Mock Instances, {
			  access(_ as Accessor) >> accessedInstances
			  getSourceAccessor() >> accessor
		  }
		  def node = new DiffNode(RootAccessor.instance, Object)

		when:
		  differDispatcher.dispatch(node, instances, accessor)

		then:
		  differDispatcher.workingCircularReferenceDetector.size() == 1
		  differDispatcher.baseCircularReferenceDetector.size() == 1
	}
}
