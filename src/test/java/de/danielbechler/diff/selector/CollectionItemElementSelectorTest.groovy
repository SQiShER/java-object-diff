package de.danielbechler.diff.selector

import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class CollectionItemElementSelectorTest extends Specification {
	def 'can be created with "null" item'() {
		expect:
		  new CollectionItemElementSelector(null) != null
	}

	def 'should equal other CollectionElement if items are equal'() {
		setup:
		  def element = new CollectionItemElementSelector("foo")
		  def equalElement = new CollectionItemElementSelector("foo")

		expect:
		  element.equals(equalElement)
	}

	def 'should not equal elements of different class'() {
		setup:
		  def element = new CollectionItemElementSelector("foo")
		  def mapElement = new MapKeyElementSelector("foo")

		expect:
		  !element.equals(mapElement)
	}

	def 'should not equal null'() {
		setup:
		  def element = new CollectionItemElementSelector("foo")

		expect:
		  !element.equals(null)
	}

	def 'should have same hashCode as item'() {
		setup:
		  def item = "foo"
		  def element = new CollectionItemElementSelector(item)

		expect:
		  element.hashCode() == item.hashCode()
	}

	def 'should provide accessor for item'() {
		setup:
		  def item = "foo"
		  def element = new CollectionItemElementSelector(item)

		expect:
		  element.getItem() == item
	}

	def 'should have proper toString() representation'() {
		expect: "string representation of the item should be converted to single line"
		  new CollectionItemElementSelector("foo\nbar").toString() == '[foo \\ bar]'
	}
}
