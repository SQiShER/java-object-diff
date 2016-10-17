package de.danielbechler.diff.node

import de.danielbechler.diff.ObjectDifferBuilder
import spock.lang.Specification


class DiffJSONVisitorTest extends Specification {

    DiffJSONVisitor visitor

    Something workingObject
    Something baseObject

    def setup() {
        workingObject = new Something(name: 'working name', someOther: new Something(name: 'working other'), theSame: 'theSame')
        baseObject = new Something(name: 'base name', someOther: new Something(name: 'base other'), theSame: 'theSame')
        visitor = new DiffJSONVisitor(workingObject, baseObject)
    }

    def "something to map"() {
        when:
        DiffNode root = ObjectDifferBuilder.buildDefault().compare(workingObject, baseObject)
        root.visit(visitor)
        Map<String, Object> messages = visitor.messagesAsMap

        then:
        messages.name == 'working name'
        messages.someOther.name == 'working other'
    }

    def "something to json"() {
        when:
        DiffNode root = ObjectDifferBuilder.buildDefault().compare(workingObject, baseObject)
        root.visit(visitor)
        String json = visitor.getAsJSON()

        then:
        json == '{"name":"working name","someOther":{"name":"working other"}}'
    }

    def "clearing a field"() {
        when:
        workingObject.name = null
        DiffNode root = ObjectDifferBuilder.buildDefault().compare(workingObject, baseObject)
        root.visit(visitor)
        String json = visitor.getAsJSON()

        then:
        json == '{"name":null,"someOther":{"name":"working other"}}'
    }

    static class Something {
        String name
        String theSame
        Something someOther
    }
}
