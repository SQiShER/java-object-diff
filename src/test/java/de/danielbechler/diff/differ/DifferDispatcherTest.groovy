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

import de.danielbechler.diff.access.Instances
import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.access.RootAccessor
import de.danielbechler.diff.category.CategoryResolver
import de.danielbechler.diff.circular.CircularReferenceDetector
import de.danielbechler.diff.circular.CircularReferenceDetectorFactory
import de.danielbechler.diff.circular.CircularReferenceExceptionHandler
import de.danielbechler.diff.filtering.IsReturnableResolver
import de.danielbechler.diff.inclusion.IsIgnoredResolver
import de.danielbechler.diff.introspection.PropertyAccessExceptionHandler
import de.danielbechler.diff.introspection.PropertyAccessExceptionHandlerResolver
import de.danielbechler.diff.introspection.PropertyReadException
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

import static de.danielbechler.diff.circular.CircularReferenceDetector.CircularReferenceException

public class DifferDispatcherTest extends Specification {

	DifferDispatcher differDispatcher

	DifferProvider differProvider = Mock()
	CircularReferenceDetector circularReferenceDetector = Mock()
	CircularReferenceDetectorFactory circularReferenceDetectorFactory = Mock()
	CircularReferenceExceptionHandler circularReferenceExceptionHandler = Mock()
	IsIgnoredResolver ignoredResolver = Mock()
	CategoryResolver categoryResolver = Mock()
	IsReturnableResolver returnableResolver = Mock()
	PropertyAccessExceptionHandlerResolver propertyAccessExceptionHandlerResolver = Mock()

	def setup() {
		circularReferenceDetectorFactory.createCircularReferenceDetector() >> circularReferenceDetector
		categoryResolver.resolveCategories(_) >> []
		differDispatcher = new DifferDispatcher(differProvider,
				circularReferenceDetectorFactory,
				circularReferenceExceptionHandler,
				ignoredResolver,
				returnableResolver,
				propertyAccessExceptionHandlerResolver,
				categoryResolver);
	}

	def 'when circular reference is detected the node should be marked as circular'() throws Exception {
		given:
		  circularReferenceDetector.push(_, _) >> { instance, nodePath ->
			  throw new CircularReferenceException(nodePath)
		  }
		when:
		  def node = differDispatcher.dispatch(DiffNode.ROOT, Instances.of('*', '*'), RootAccessor.instance);
		then:
		  node.state == DiffNode.State.CIRCULAR
	}

	def 'when circular reference is detected the node should hold the path to the node it circles back to'() throws Exception {
		given:
		  circularReferenceDetector.push(_, _) >> { instance, nodePath ->
			  throw new CircularReferenceException(nodePath)
		  }
		when:
		  def node = differDispatcher.dispatch(DiffNode.ROOT, Instances.of('*', '*'), RootAccessor.instance);
		then: 'the node should be marked as circular'
		  node.circleStartPath == NodePath.withRoot()
		and:
		  1 * circularReferenceExceptionHandler.onCircularReferenceException(_ as DiffNode)
	}

	def 'when circular reference is detected the node should be passed to the exception handler before it is returned'() throws Exception {
		def handledNode = null

		given:
		  circularReferenceDetector.push(_, _) >> { instance, nodePath ->
			  throw new CircularReferenceException(nodePath)
		  }
		when:
		  def node = differDispatcher.dispatch(DiffNode.ROOT, Instances.of('*', '*'), RootAccessor.instance);
		then:
		  1 * circularReferenceExceptionHandler.onCircularReferenceException(_ as DiffNode) >> { DiffNode it -> handledNode = it }
		expect:
		  node.is(handledNode)
	}

	def 'throw exception if no differ can be found for instance type'() {
		given:
		  differProvider.retrieveDifferForType(_) >> null
		when:
		  differDispatcher.dispatch(DiffNode.ROOT, Instances.of('*', '*'), RootAccessor.instance);
		then:
		  thrown(IllegalStateException)
	}

	def 'should delegate property read exception to PropertyAccessExceptionHandler'() {
		def propertyExceptionHandler = Mock PropertyAccessExceptionHandler
		def propertyAccessor = Mock(PropertyAwareAccessor) {
			getPropertyName() >> 'foo'
		}
		def propertyAccessException = new PropertyReadException('foo', Date, new RuntimeException())
		def instances = Mock Instances, {
			access(propertyAccessor) >> { throw propertyAccessException }
			getType() >> Date
		}
		when:
		  differDispatcher.dispatch(DiffNode.ROOT, instances, propertyAccessor)
		then:
		  1 * propertyAccessExceptionHandlerResolver.resolvePropertyAccessExceptionHandler(Date, 'foo') >> propertyExceptionHandler
		and:
		  1 * propertyExceptionHandler.onPropertyReadException(propertyAccessException, _ as DiffNode)
	}

	def 'when reading a property value caused an exception the returned node should have state INACESSIBLE'() {
		def propertyAccessor = Mock(PropertyAwareAccessor)
		def instances = Mock Instances, {
			access(propertyAccessor) >> {
				throw new PropertyReadException('foo', Date, new RuntimeException())
			}
		}
		when:
		  def node = differDispatcher.dispatch(DiffNode.ROOT, instances, propertyAccessor)
		then:
		  node.state == DiffNode.State.INACCESSIBLE
	}
}
