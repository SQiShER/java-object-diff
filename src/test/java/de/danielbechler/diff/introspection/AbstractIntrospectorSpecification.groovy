package de.danielbechler.diff.introspection

import de.danielbechler.diff.access.PropertyAwareAccessor
import spock.lang.Specification

abstract class AbstractIntrospectorSpecification extends Specification {

	abstract Introspector getIntrospector()

	def 'private field accessible via getter and setter'() {
		given:
		def testType = new IntrospectorTestType()

		when:
		def typeInfo = introspector.introspect(IntrospectorTestType)
		def accessor = getAccessorByName(typeInfo.accessors, 'readWriteProperty')

		then:
		accessor != null

		and:
		accessor.fieldAnnotations.size() == 1
		def fieldAnnotation = accessor.fieldAnnotations.first() as ObjectDiffProperty
		fieldAnnotation.categories().first() == 'field'

		and:
		accessor.readMethodAnnotations.size() == 1
		def getterAnnotation = accessor.readMethodAnnotations.first() as ObjectDiffProperty
		getterAnnotation.categories().first() == 'getter'

		and:
		accessor.categoriesFromAnnotation == ['getter'] as Set

		when:
		def value1 = UUID.randomUUID().toString()
		accessor.set(testType, value1)

		then:
		testType.getReadWriteProperty() == value1

		when:
		def value2 = UUID.randomUUID().toString()
		testType.setReadWriteProperty(value2)

		then:
		accessor.get(testType) == value2
	}

	def 'private field accessible via getter and ambiguous setters'() {
		given:
		def testType = new IntrospectorTestType()

		when:
		def typeInfo = introspector.introspect(IntrospectorTestType)
		def accessor = getAccessorByName(typeInfo.accessors, 'ambiguousSetterProperty')

		then:
		accessor != null

		when:
		def value1 = Integer.valueOf(10)
		accessor.set(testType, value1)

		then:
		testType.getAmbiguousSetterProperty() == value1

		when:
		def value2 = Math.random()
		testType.setAmbiguousSetterProperty(value2 as Number)

		then:
		accessor.get(testType) == value2
	}

	def 'private field only accessible via getter'() {
		given:
		def testType = new IntrospectorTestType()

		when:
		def typeInfo = introspector.introspect(IntrospectorTestType)
		def accessor = getAccessorByName(typeInfo.accessors, 'readOnlyProperty')

		then:
		accessor != null

		and:
		accessor.fieldAnnotations.size() == 1
		def fieldAnnotation = accessor.fieldAnnotations.first() as ObjectDiffProperty
		fieldAnnotation.categories().first() == 'field'

		and:
		accessor.readMethodAnnotations.size() == 1
		def getterAnnotation = accessor.readMethodAnnotations.first() as ObjectDiffProperty
		getterAnnotation.categories().first() == 'getter'

		when:
		def initialReadOnlyPropertyValue = testType.getReadOnlyProperty()
		def newReadOnlyPropertyValue = UUID.randomUUID().toString()
		accessor.set(testType, newReadOnlyPropertyValue)

		then:
		testType.getReadOnlyProperty() == initialReadOnlyPropertyValue

		expect:
		accessor.get(testType) == testType.getReadOnlyProperty()
	}

	static PropertyAwareAccessor getAccessorByName(Collection<PropertyAwareAccessor> accessors, String propertyName) {
		return accessors.find { it.propertyName == propertyName }
	}
}
