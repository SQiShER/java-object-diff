package de.danielbechler.diff.collection

import de.danielbechler.diff.map.MapElement
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class CollectionElementTest extends Specification
{
  def 'can be created with "null" item'()
  {
    expect:
    new CollectionElement(null) != null
  }

  def 'should equal other CollectionElement if items are equal'()
  {
    setup:
    def element = new CollectionElement("foo")
    def equalElement = new CollectionElement("foo")

    expect:
    element.equals(equalElement)
  }

  def 'should not equal elements of different class'()
  {
    setup:
    def element = new CollectionElement("foo")
    def mapElement = new MapElement("foo")

    expect:
    !element.equals(mapElement)
  }

  def 'should not equal null'()
  {
    setup:
    def element = new CollectionElement("foo")

    expect:
    !element.equals(null)
  }

  def 'should have same hashCode as item'()
  {
    setup:
    def item = "foo"
    def element = new CollectionElement(item)

    expect:
    element.hashCode() == item.hashCode()
  }

  def 'should provide accessor for item'()
  {
    setup:
    def item = "foo"
    def element = new CollectionElement(item)

    expect:
    element.getItem() == item
  }

  def 'should have proper toString() representation'()
  {
    expect: "string representation of the item should be converted to single line"
    new CollectionElement("foo\nbar").toString() == '[foo \\ bar]'
  }
}
