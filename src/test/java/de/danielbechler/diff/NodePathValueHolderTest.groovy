package de.danielbechler.diff

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Daniel Bechler
 */
class NodePathValueHolderTest extends Specification
{
  def "should allow creation of new instances"()
  {
    expect:
    NodePathValueHolder.of(String) instanceof NodePathValueHolder
  }

  @Unroll
  def "should store value '#value' at node path '#nodePath'"()
  {
    given:
    def valueHolder = NodePathValueHolder.of(String)
    valueHolder.put(nodePath, value);

    expect:
    valueHolder.valueForNodePath(nodePath) == value;

    where:
    nodePath                     | value
    NodePath.withRoot()     | "foo1"
    NodePath.with("a")      | "foo2"
    NodePath.with("a", "b") | "foo3"
  }

  def "should return null for unknown paths"()
  {
    given:
    def valueHolder = NodePathValueHolder.of(String)

    expect:
    valueHolder.valueForNodePath(NodePath.with("a")) == null
  }

  def "should return accumulated values along path"()
  {
    given:
    def valueHolder = NodePathValueHolder.of(String)
    valueHolder.put(NodePath.withRoot(), "foo1")
    valueHolder.put(NodePath.with("a"), "foo2")
    valueHolder.put(NodePath.with("a", "b"), "foo3")

    expect:
    valueHolder.accumulatedValuesForNodePath(NodePath.with("a", "b")) == ["foo1", "foo2", "foo3"]
  }
}
