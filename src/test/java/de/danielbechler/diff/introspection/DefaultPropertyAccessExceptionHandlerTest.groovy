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

package de.danielbechler.diff.introspection

import de.danielbechler.diff.node.DiffNode
import org.slf4j.Logger
import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
class DefaultPropertyAccessExceptionHandlerTest extends Specification {
	def exceptionHandler
	def diffNode = Mock(DiffNode)
	def logger = Mock(Logger)

	def setup() {
		exceptionHandler = new DefaultPropertyAccessExceptionHandler()
		exceptionHandler.logger = logger
	}

	def 'onPropertyReadException should rethrow the PropertyReadException'() {
		given:
		  def readException = new PropertyReadException('foo', Object, null)
		when:
		  exceptionHandler.onPropertyReadException(readException, diffNode)
		then:
		  def thrownException = thrown(PropertyReadException)
		  thrownException.is readException
	}

	def 'onPropertyWriteException should rethrow the PropertyWriteException'() {
		given:
		  def writeException = new PropertyWriteException('foo', Object, 'bar', null)
		when:
		  exceptionHandler.onPropertyWriteException(writeException, diffNode)
		then:
		  def thrownException = thrown(PropertyWriteException)
		  thrownException.is writeException
	}

	def 'onPropertyWriteException should log exception message'() {
		given:
		  def writeException = new PropertyWriteException('foo', Object, 'bar', null)
		when:
		  exceptionHandler.onPropertyWriteException(writeException, diffNode)
		then:
		  1 * logger.info(writeException.message);
		and:
		  thrown(PropertyWriteException)
	}
}
