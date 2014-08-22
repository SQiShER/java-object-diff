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

package de.danielbechler.diff

import de.danielbechler.diff.differ.Differ
import de.danielbechler.diff.differ.DifferDispatcher
import de.danielbechler.diff.differ.DifferFactory
import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
@SuppressWarnings("GroovyAssignabilityCheck")
class ObjectDifferBuilderIT extends Specification {
	def differMock = Mock(Differ)

	def 'Register a DifferExtension'() {
		given:
		  def differFactory = new DifferFactory() {
			  @Override
			  Differ createDiffer(DifferDispatcher differDispatcher, NodeQueryService nodeQueryService) {
				  return differMock
			  }
		  }
		  def objectDiffer = ObjectDifferBuilder.startBuilding()
				  .differs().register(differFactory)
				  .build()

		when:
		  objectDiffer.compare("foo", "bar")

		then:
		  1 * differMock.accepts(*_) >> true

		and:
		  1 * differMock.compare(*_)
	}
}
