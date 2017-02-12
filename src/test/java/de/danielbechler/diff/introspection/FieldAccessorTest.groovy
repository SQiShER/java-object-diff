package de.danielbechler.diff.introspection

import spock.lang.Specification
import spock.lang.Subject

import java.lang.annotation.Annotation
import java.lang.reflect.Field

class FieldAccessorTest extends Specification {

	@Subject
	FieldAccessor fieldAccessor

	def setup() {
		Field field = DtoForTesting.class.fields.find { Field field -> field.name == 'publicField' }
		fieldAccessor = new FieldAccessor(field)
	}

	def 'getPropertyName'() {
		expect:
		fieldAccessor.propertyName == 'publicField'
	}

	def 'getType'() {
		expect:
		fieldAccessor.type == String
	}

	def 'get'() {
		setup:
		String expectedValue = UUID.randomUUID().toString()
		DtoForTesting dto = new DtoForTesting('foo')
		dto.publicField = expectedValue
		expect:
		fieldAccessor.get(dto) == expectedValue
	}

	def 'set'() {
		given:
		DtoForTesting dto = new DtoForTesting('foo')
		when:
		String expectedValue = UUID.randomUUID().toString()
		fieldAccessor.set(dto, expectedValue)
		then:
		dto.publicField == expectedValue
	}

	def 'set should be able to change value of static field'() {
		given:
		fieldAccessor = new FieldAccessor(DtoForTesting.class.fields.find { Field field ->
			field.name == 'publicFinalField'
		})
		DtoForTesting dto = new DtoForTesting('foo')
		when:
		fieldAccessor.set(dto, 'bar')
		then:
		dto.publicFinalField == 'bar'
	}

	def 'getFieldAnnotations'() {
		when:
		Set<Annotation> annotations = fieldAccessor.fieldAnnotations
		then:
		annotations.size() == 1
		and:
		ObjectDiffProperty annotation = annotations.first() as ObjectDiffProperty
		annotation.categories() as List == ['foo']
	}

	def 'getFieldAnnotation'() {
		setup:
		def annotation = fieldAccessor.getFieldAnnotation(ObjectDiffProperty)
		expect:
		annotation != null
	}

	def 'getReadMethodAnnotations'() {
		expect:
		fieldAccessor.readMethodAnnotations == [] as Set
	}

	def 'getReadMethodAnnotation'() {
		expect:
		fieldAccessor.getReadMethodAnnotation(ObjectDiffProperty) == null
	}

	def 'isExcludedByAnnotation'() {
		expect:
		fieldAccessor.isExcludedByAnnotation()
	}
}
