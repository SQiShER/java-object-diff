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

package de.danielbechler.diff.example

import de.danielbechler.diff.ObjectDiffer
import de.danielbechler.diff.ObjectDifferBuilder
import spock.lang.Specification

import java.nio.charset.Charset

class CustomDifferExampleIT extends Specification {

	class User {
		int id
		String name
		byte[] avatar
	}

	def 'equals-only byte array diff via custom Differ'() {
		given:
		  def originalUser = new User(id: 1, name: 'Foo', avatar: 'Test'.getBytes(Charset.forName('utf-8')))
		  def updatedUser = new User(id: 1, name: 'Foo', avatar: 'New Avatar'.getBytes(Charset.forName('utf-8')))
		  ObjectDiffer objectDiffer = ObjectDifferBuilder.startBuilding()
				  .differs().register(new ByteArrayDiffer.Factory())
				  .build()
		  def node = objectDiffer.compare(updatedUser, originalUser)
		expect:
		  node.getChild('avatar').changed
		  node.getChild('avatar').hasChildren() == false
		when:
		  node.getChild('avatar').set(originalUser, 'Even newer avatar'.getBytes(Charset.forName('utf-8')))
		then:
		  node.getChild('avatar').get(originalUser) == 'Even newer avatar'.getBytes(Charset.forName('utf-8'))
	}
}
