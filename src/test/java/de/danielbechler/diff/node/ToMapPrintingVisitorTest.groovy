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

package de.danielbechler.diff.node

import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

public class ToMapPrintingVisitorTest extends Specification {

	ToMapPrintingVisitor visitor

	def setup() {
		visitor = new ToMapPrintingVisitor("foo", "bar")
	}

	def 'constructor works with null values'() {
		when:
		  new ToMapPrintingVisitor(null, null)
		then:
		  noExceptionThrown()
	}

	def 'getMessages returns Map with accepted messages'() {
		given:
		  DiffNode node = DiffNode.newRootNodeWithType(String)
		  node.state = DiffNode.State.CHANGED
		when:
		  node.visit(visitor)
		then:
		  def messages = visitor.messages
		  messages.size() == 1
		  messages.containsKey(NodePath.withRoot())
	}

	def 'getMessages returns empty map if no messages have been accepted'() {
		expect:
		  visitor.messages.isEmpty()
	}

	def 'getMessages returns modifiable map when messages exist'() {
		given:
		  DiffNode node = DiffNode.newRootNodeWithType(String)
		  node.state = DiffNode.State.CHANGED
		  node.visit(visitor)
		when:
		  def previousSize = visitor.messages.size()
		  visitor.messages.put(NodePath.with("foo"), "bar")
		then:
		  visitor.messages.size() == previousSize + 1
	}

	def 'getMessages returns modifiable map when no messages exist'() {
		when:
		  visitor.messages.put(NodePath.with("foo"), "bar")
		then:
		  visitor.messages.size() == 1
	}

	def 'getMessage returns message when messages are present'() {
		given:
		  DiffNode node = DiffNode.newRootNodeWithType(String)
		  node.state = DiffNode.State.CHANGED
		when:
		  node.visit(visitor)
		then:
		  visitor.getMessage(NodePath.withRoot()).length() > 0
	}

	def 'getMessage_returns_null_when_message_absend'() {
		given:
		  DiffNode node = DiffNode.newRootNodeWithType(String)
		  node.setState(DiffNode.State.CHANGED)
		when:
		  node.visit(visitor)
		then:
		  visitor.getMessage(NodePath.with("doesn't-exist")) == null
	}

	def 'hasMessages returns true when messages exist'() {
		given:
		  DiffNode node = DiffNode.newRootNodeWithType(String)
		  node.state = DiffNode.State.CHANGED
		when:
		  node.visit(visitor)
		then:
		  visitor.hasMessages() == true
	}

	def 'hasMessages returns false when no messages exist'() {
		expect:
		  visitor.hasMessages() == false
	}

	def 'getMessagesAsString returns line break separated list of messages when messages exist'() {
		given:
		  DiffNode node = DiffNode.newRootNodeWithType(String)
		  node.state = DiffNode.State.CHANGED
		when:
		  node.visit(visitor)
		then:
		  visitor.messagesAsString == visitor.getMessage(NodePath.withRoot()) + '\n'
	}

	def 'getMessagesAsString returns empty string when no messages exist'() {
		expect:
		  visitor.messagesAsString.isEmpty()
	}

	def 'toString is analogous to getMessagesAsString'() {
		given:
		  DiffNode node = DiffNode.newRootNodeWithType(String)
		  node.state = DiffNode.State.CHANGED
		when:
		  node.visit(visitor)
		then:
		  visitor.toString() == visitor.messagesAsString
	}

	def 'clear removes all messages'() {
		given:
		  visitor.messages.put(NodePath.withRoot(), 'foo')
		when:
		  visitor.clear()
		then:
		  visitor.hasMessages() == false
	}
}
