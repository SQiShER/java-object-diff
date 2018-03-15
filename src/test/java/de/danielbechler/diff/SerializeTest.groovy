/*
 * Copyright 2018 Jamin Collins
 */
package de.danielbechler.diff

import de.danielbechler.diff.node.DiffNode
import spock.lang.*

public class SerializeTest extends Specification {
    DiffNode diff = null
    ObjectOutputStream oos;
    ByteArrayOutputStream bos = new ByteArrayOutputStream()

    def setup() {
        diff = ObjectDifferBuilder.buildDefault().compare("1", "2")
        oos = new ObjectOutputStream(bos)
        oos.writeObject(diff)
        oos.flush()
    }
    def "ensure serialization works"() {
        expect:
            bos.toByteArray().length > 0
    }
}
