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

package de.danielbechler.diff.inclusion

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.introspection.ObjectDiffProperty
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.CollectionItemElementSelector
import groovy.transform.EqualsAndHashCode
import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class InclusionAT extends Specification {

	def objectDifferBuilder = ObjectDifferBuilder.startBuilding()
	def base = new Album(artist: new Artist(name: 'Pharrell Williams'), songs: [new Song(durationInSeconds: 130, title: 'Happy')])
	def working = new Album(artist: new Artist(name: 'N.E.R.D.'), songs: [new Song(durationInSeconds: 150, title: 'It')])

	def 'exclude an element by type'() {
		given:
		  objectDifferBuilder.inclusion().exclude().type(ArrayList)
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs') == null
		  node.getChild('artist').changed
	}

	def 'exclude an element by property name'() {
		given:
		  objectDifferBuilder.inclusion().exclude().propertyName('songs')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs') == null
		  node.getChild('artist').changed
	}

	def 'exclude an element by node path'() {
		given:
		  objectDifferBuilder.inclusion().exclude().node(NodePath.with('songs'))
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs') == null
		  node.getChild('artist').changed
	}

	def 'exclude an element by category'() {
		given:
		  objectDifferBuilder.inclusion().exclude().category('foo')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs') == null
		  node.getChild('artist').changed
	}

	def 'include an element by type'() {
		given:
		  objectDifferBuilder.inclusion().include().type(ArrayList).type(Song)
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').changed
		  node.getChild('artist') == null
	}

	def 'include an element by property name'() {
		given:
		  objectDifferBuilder.inclusion().include().propertyName('songs')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').changed
		  node.getChild('artist') == null
	}

	def 'include an element by node path'() {
		given:
		  objectDifferBuilder.inclusion().include().node(NodePath.with('songs'))
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').changed
		  node.getChild('artist') == null
	}

	def 'include an element by category'() {
		given:
		  objectDifferBuilder.inclusion().include().category('foo')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').changed
		  node.getChild('artist') == null
	}

	def 'excludes always win over includes'() {
		given:
		  objectDifferBuilder.inclusion()
				  .exclude().node(NodePath.with('songs')).also()
				  .include().node(NodePath.startBuildingFrom(NodePath.with('songs')).collectionItem('Happy').build())
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.childCount() == 0
	}

	def 'including an element implicitly excludes its siblings'() {
		given:
		  objectDifferBuilder.inclusion().include().propertyName('artist')
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild('artist').changed
	}

	def 'including an element implicitly includes its children'() {
		given:
		  objectDifferBuilder.inclusion().include().node(NodePath.with('songs'))
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').getChild(new CollectionItemElementSelector(new Song(title: 'Happy'))).removed
		  node.getChild('songs').getChild(new CollectionItemElementSelector(new Song(title: 'It'))).added
	}

	def 'including an element by path implicitly includes its parents'() {
		given:
		  def includedNodePath = NodePath.startBuilding()
				  .propertyName('songs')
				  .collectionItem(new Song(title: 'Happy'))
		  objectDifferBuilder.inclusion().include().node(includedNodePath.build())
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('songs').getChild(new CollectionItemElementSelector(new Song(title: 'Happy'))).removed
		  node.getChild('songs').childCount() == 1
	}

	class Album {
		def Artist artist
		def ArrayList<Song> songs

		@ObjectDiffProperty(categories = ['foo'])
		ArrayList<Song> getSongs() {
			return songs
		}

		void setSongs(ArrayList<Song> songs) {
			this.songs = songs
		}
	}

	@EqualsAndHashCode(includeFields = true)
	class Artist {
		def String name
	}

	@EqualsAndHashCode(includeFields = true, includes = ['title'])
	class Song {
		def String title
		def int durationInSeconds
	}
}
