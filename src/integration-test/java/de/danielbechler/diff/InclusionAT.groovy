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

import de.danielbechler.diff.annotation.ObjectDiffProperty
import de.danielbechler.diff.collection.CollectionItemElementSelector
import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
class InclusionAT extends Specification {
	def objectDifferBuilder = ObjectDifferBuilder.startBuilding()
	def base = new Album(artist: 'Pharrell Williams', songs: ['Happy'])
	def working = new Album(artist: 'N.E.R.D.', songs: ['It'])

	class Album {
		def String artist
		def ArrayList<String> songs

		@ObjectDiffProperty(categories = ['foo'])
		ArrayList<String> getSongs() {
			return songs
		}

		void setSongs(ArrayList<String> songs) {
			this.songs = songs
		}
	}

	def 'exclude an element by type'() {
		given:
		  objectDifferBuilder.configure().inclusion().toExclude().types(ArrayList)
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs') == null
		  node.getChild('artist').changed
	}

	def 'exclude an element by property name'() {
		given:
		  objectDifferBuilder.configure().inclusion().toExclude().propertyNames('songs')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs') == null
		  node.getChild('artist').changed
	}

	def 'exclude an element by node path'() {
		given:
		  objectDifferBuilder.configure().inclusion().toExclude().node(NodePath.with('songs'))
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs') == null
		  node.getChild('artist').changed
	}

	def 'exclude an element by category'() {
		given:
		  objectDifferBuilder.configure().inclusion().toExclude().categories('foo')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs') == null
		  node.getChild('artist').changed
	}

	def 'include an element by type'() {
		given:
		  objectDifferBuilder.configure().inclusion().toInclude().types(ArrayList)
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').changed
		  node.getChild('artist') == null
	}

	def 'include an element by property name'() {
		given:
		  objectDifferBuilder.configure().inclusion().toInclude().propertyNames('songs')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').changed
		  node.getChild('artist') == null
	}

	def 'include an element by node path'() {
		given:
		  objectDifferBuilder.configure().inclusion().toInclude().node(NodePath.with('songs'))
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').changed
		  node.getChild('artist') == null
	}

	def 'include an element by category'() {
		given:
		  objectDifferBuilder.configure().inclusion().toInclude().categories('foo')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').changed
		  node.getChild('artist') == null
	}

	def 'excludes always win over includes'() {
		given:
		  def songs = NodePath.with('songs')
		  objectDifferBuilder.configure().inclusion().toExclude().node(songs)
		and:
		  objectDifferBuilder.configure().inclusion().toInclude().node(NodePath.startBuildingFrom(songs).collectionItem('Happy').build())
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.childCount() == 0
	}

	def 'including an element implicitly excludes all others'() {
		given:
		  objectDifferBuilder.configure().inclusion().toInclude().propertyNames('artist')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild('artist').changed
	}

	def 'including an element implicitly includes its children'() {
		given:
		  objectDifferBuilder.configure().inclusion().toInclude().node(NodePath.with('songs'))
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').getChild(new CollectionItemElementSelector('Happy')).removed
		  node.getChild('songs').getChild(new CollectionItemElementSelector('It')).added
	}
}
