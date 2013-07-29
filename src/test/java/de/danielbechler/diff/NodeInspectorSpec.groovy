package de.danielbechler.diff

import de.danielbechler.diff.path.PropertyPath
import spock.lang.Specification
/**
 * @author Daniel Bechler
 */
class NodeInspectorSpec extends Specification
{
  def "isIncluded should include children of included properties"()
  {
    given: "a configuration with explicitly included property"
    Configuration configuration = new Configuration()
    configuration.withPropertyPath(PropertyPath.buildWith("collection"))

    and: "a node that represents a child of the included property"
    de.danielbechler.diff.node.Node node = Mock(de.danielbechler.diff.node.Node)
    node.isRootNode() >> false
    node.getPropertyPath() >> PropertyPath.buildWith("collection", "child")

    expect:
    configuration.isIncluded(node)
  }
}
