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

import de.danielbechler.diff.comparison.PrimitiveDefaultValueMode;
import de.danielbechler.diff.mock.*
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.node.DiffNode.State
import de.danielbechler.diff.node.Visit
import de.danielbechler.diff.node.DiffNode.Visitor
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.CollectionItemElementSelector
import de.danielbechler.diff.selector.MapKeyElementSelector
import spock.lang.Ignore
import spock.lang.Specification

public class ObjectDifferIT extends Specification {

	def objectDiffer = ObjectDifferBuilder.buildDefault()

	def 'compare bean with ignored map property'() {
		given:
		  def working = new ObjectWithIgnoredMap(map: [foo: 'bar'])
		  def base = new ObjectWithIgnoredMap()
		when:
		  def node = objectDiffer.compare(working, base)
		then:
		  node.untouched == true
		  node.hasChildren() == false
	}

	def 'compare collection with ignored collection property'() {
		given:
		  def working = new ObjectWithCollection(collection: ['foo'])
		and:
		  def objectDiffer = ObjectDifferBuilder.startBuilding()
				  .inclusion()
				  .exclude().node(NodePath.with('collection'))
				  .and()
				  .build()
		when:
		  def node = objectDiffer.compare(working, new ObjectWithCollection())
		then:
		  node.untouched
		  node.childCount() == 0
	}

	def 'compare collection with added item'() {
		given:
		  def working = ['foo']
		  def base = []
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.changed
		and:
		  node.getChild(new CollectionItemElementSelector('foo')).added
	}

	def 'compare collection with removed item'() {
		given:
		  def working = []
		  def base = ['foo']
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.changed
		and:
		  node.getChild(new CollectionItemElementSelector('foo')).removed
	}

	def 'ignored properties are never accessed'() {
		given:
		  def working = new ObjectWithAccessTrackingIgnoredProperty()
		  def base = new ObjectWithAccessTrackingIgnoredProperty()
		when:
		  ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  !working.accessed
		  !base.accessed
	}

	def 'object graph for added objects gets returned'() {
		given:
		  def workingChildChild = new ObjectWithNestedObject('foo')
		  def workingChild = new ObjectWithNestedObject('2', workingChildChild)
		  def working = new ObjectWithNestedObject('1', workingChild)
		  def base = new ObjectWithNestedObject('1')
		when:
		  def node = ObjectDifferBuilder.startBuilding().build().compare(working, base)
		then:
		  node.changed
		  node.getChild('object').added
		  node.getChild('object').getChild('object').added
	}

	def 'compare collection with different collection implementations succeeds'() {
		given:
		  def working = ['one', 'three'] as TreeSet
		  def base = ['one', 'two'] as LinkedHashSet
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.changed
	}

	def 'compare collection with changed item'() {
		given:
		  def working = [new ObjectWithIdentityAndValue("foo", "1")]
		  def base = [new ObjectWithIdentityAndValue("foo", "2")]
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.hasChanges()
		and:
		  node.getChild(new CollectionItemElementSelector(new ObjectWithIdentityAndValue("foo"))).changed
	}

	def 'with new map in working and none in base'() {
		given:
		  def working = [foo: 'bar'] as TreeMap
		  def base = null
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.added
		  node.getChild(new MapKeyElementSelector('foo')).added
	}

	def 'with addition of simple type to working map'() {
		given:
		  def working = [foo: 'bar']
		  def base = [:]
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.changed
		  node.getChild(new MapKeyElementSelector('foo')).added
	}

	def 'with same entry in base and working'() {
		given:
		  def base = [foo: 'bar']
		  def working = [foo: 'bar']
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.untouched
		  node.childCount() == 0
	}

	def 'with single entry in base and missing working'() {
		given:
		  def base = [foo: 'bar']
		  def working = null
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.removed
		  node.getChild(new MapKeyElementSelector('foo')).removed
	}

	/**
	 * Ensures that the map can handle null values in both,
	 * the base and the working version, in which case no
	 * type can be detected.
	 */
	def 'with all null map item'() {
		given:
		  def working = [foo: null]
		  def base = [foo: null]
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.untouched
	}

	def 'with same entries'() {
		given:
		  def working = [foo: 'bar']
		  def base = working.clone()
		  working.put('ping', 'pong')
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild(new MapKeyElementSelector('foo')) == null
		and:
		  with(node.getChild(new MapKeyElementSelector('ping'))) { pingNode ->
			  assert pingNode.added
			  assert pingNode.childCount() == 0
		  }
	}

	def 'with changed entry'() {
		given:
		  def working = [foo: 'bar']
		  def base = [foo: 'woot']
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild(new MapKeyElementSelector("foo")).changed
	}

	def 'compare with different map implementations succeeds'() {
		given:
		  def base = [test: 'foo'] as LinkedHashMap
		  def working = [test: 'bar'] as TreeMap
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.changed
	}

	def 'compare with different strings'() {
		expect:
		  ObjectDifferBuilder.buildDefault().compare('foo', 'bar').changed
	}

	def 'compare with different types'() {
		when:
		  ObjectDifferBuilder.buildDefault().compare('foo', 1337)
		then:
		  thrown IllegalArgumentException
	}

	def 'compare with ignored property'() {
		given:
		  def working = new ObjectWithIdentityAndValue('1', 'foo')
		  def base = new ObjectWithIdentityAndValue('1', 'bar')
		when:
		  def objectDiffer = ObjectDifferBuilder.startBuilding()
				  .inclusion()
				  .exclude().node(NodePath.with('value')).and()
				  .filtering().returnNodesWithState(DiffNode.State.IGNORED).and()
				  .build()
		  def node = objectDiffer.compare(working, base)
		then:
		  node.getChild('value').state == DiffNode.State.IGNORED
	}

	def 'compare with complex type'() {
		given:
		  def working = new ObjectWithIdentityAndValue('a', '1')
		  def base = new ObjectWithIdentityAndValue('a', '2')
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.changed
	}

	@Ignore('Currently this is simply not possible because of the way, the CollectionItemAccessor works. Would be great, to support this.')
	def 'compare with list containing object twice detects if one gets removed'() {
		given:
		  def base = [
				  new ObjectWithHashCodeAndEquals('foo'),
				  new ObjectWithHashCodeAndEquals('foo')
		  ]
		  def working = [
				  new ObjectWithHashCodeAndEquals('foo')
		  ]
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.getChild(new ObjectWithHashCodeAndEquals('foo')).removed
	}

	@Ignore('Currently this is simply not possible because of the way, the CollectionItemAccessor works. Would be great, to support this.')
	def 'compare with list containing object once detects if another instance of it gets added'() {
		given:
		  def base = [
				  new ObjectWithHashCodeAndEquals('foo'),
		  ]
		  def working = [
				  new ObjectWithHashCodeAndEquals('foo'),
				  new ObjectWithHashCodeAndEquals('foo')
		  ]
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.getChild(new ObjectWithHashCodeAndEquals('foo')).added
	}

	def 'compare bean with equals only value provider method on get collection property no change in method result'() {
		given:
		  def working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(['one'])
		  def base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(['uno'])
		when:
		  def node = objectDiffer.compare(working, base);
		then:
		  node.untouched
		  node.childCount() == 0
	}

	def 'compare bean with equal only value provider method on get collection property with change in method result'() {
		given:
		  final working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(['one', 'two'])
		  final base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(['uno'])
		when:
		  final node = objectDiffer.compare(working, base)
		then:
		  node.changed
		  node.getChild('collection').changed
	}

	def 'compare bean with equals only value provider method on get object property no change in method result'() {
		given:
		  def working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject("id", new ObjectWithNestedObject('childid', new ObjectWithNestedObject("grandchildid")));
		  def base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject("id", new ObjectWithNestedObject('childid', new ObjectWithNestedObject("differentgrandchildid")));
		when:
		  final DiffNode node = objectDiffer.compare(working, base)
		then:
		  node.untouched
		  node.childCount() == 0
	}

	def 'compare bean with equals only value provider method on get object property with change in method result'() {
		given:
		  def working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject('id',
				  new ObjectWithNestedObject('childid',
						  new ObjectWithNestedObject('grandchildid')))
		and:
		  def base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject('id',
				  new ObjectWithNestedObject('differentchildid',
						  new ObjectWithNestedObject('differentgrandchildid')))
		when:
		  def node = objectDiffer.compare(working, base)
		then:
		  node.changed
		  node.getChild('object').changed
	}

	def 'compare bean with equals only value provider method on get map property no change in method result'() {
		given:
		  final working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap([key1: 'val1'])
		  final base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap([keyone: 'valone'])
		when:
		  final node = objectDiffer.compare(working, base)
		then:
		  node.untouched
		  node.childCount() == 0
	}

	def 'compare bean with equals only value provider method on get map property with change in method result'() {
		given:
		  final working = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap([key1: 'val1', key2: 'val2'])
		  final base = new ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap([keyone: 'valone'])
		when:
		  final node = objectDiffer.compare(working, base)
		then:
		  node.changed
		  node.getChild('map').changed
	}
	
	def "node should resolve State.REMOVED instead of State.ADDED"() {
		given:
			def differ = ObjectDifferBuilder.startBuilding().build()
			def working = new ObjectWithCollectionOfComplexTypes()
			def base = new ObjectWithCollectionOfComplexTypes()
			def o = new ObjectWithPrimitivePropertyAndHashCodeAndEquals()
			def visitor = new Visitor(){
				def List<DiffNode> nodes = new ArrayList<>();
				public void node(DiffNode node, Visit visit) {
					if(node.hasChanges()){
						nodes.add(node);
					}
				}
			}		
			base.getList().add(o)
		when:
			def node = differ.compare(working,base)
			node.visit(visitor)
			def list = visitor.getNodes();
		then:
		   node.hasChanges()
		   !list.empty
		   list.size() == 4
		   list[0].state == State.CHANGED
		   list[1].state == State.CHANGED
		   list[2].state == State.REMOVED
		   list[3].state == State.REMOVED					
	}	
}
