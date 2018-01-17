package de.danielbechler.diff.introspection

import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.instantiation.TypeInfo
import spock.lang.Specification

class FieldIntrospectorTest extends Specification {

	FieldIntrospector introspector = new FieldIntrospector()

	def 'should not return accessors for final fields if deactivated'() {
		given:
		introspector.setReturnFinalFields(false)
		when:
		TypeInfo result = introspector.introspect(DtoForTesting.class)
		then:
		result.accessors.find { PropertyAwareAccessor accessor -> accessor.propertyName == 'publicFinalField' } == null
		result.accessors.find { PropertyAwareAccessor accessor -> accessor.propertyName == 'publicField' } != null
	}

	def 'should return accessors for final fields if activated'() {
		given:
		introspector.setReturnFinalFields(true)
		when:
		TypeInfo result = introspector.introspect(DtoForTesting.class)
		then:
		result.accessors.find { PropertyAwareAccessor accessor -> accessor.propertyName == 'publicFinalField' } != null
		result.accessors.find { PropertyAwareAccessor accessor -> accessor.propertyName == 'publicField' } != null
	}

	def 'should returns accessors for public fields'() {
		when:
		TypeInfo result = introspector.introspect(DtoForTesting.class)
		then:
		result.accessors.size() == 1
		result.accessors.first().propertyName == 'publicField'
	}
}
