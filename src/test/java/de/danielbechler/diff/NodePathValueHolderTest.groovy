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
    NodePath.buildRootPath()     | "foo1"
    NodePath.buildWith("a")      | "foo2"
    NodePath.buildWith("a", "b") | "foo3"
  }

  def "should return null for unknown paths"()
  {
    given:
    def valueHolder = NodePathValueHolder.of(String)

    expect:
    valueHolder.valueForNodePath(NodePath.buildWith("a")) == null
  }

  def "should return accumulated values along path"()
  {
    given:
    def valueHolder = NodePathValueHolder.of(String)
    valueHolder.put(NodePath.buildRootPath(), "foo1")
    valueHolder.put(NodePath.buildWith("a"), "foo2")
    valueHolder.put(NodePath.buildWith("a", "b"), "foo3")

    expect:
    valueHolder.accumulatedValuesForNodePath(NodePath.buildWith("a", "b")) == ["foo1", "foo2", "foo3"]
  }
}
