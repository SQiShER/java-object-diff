package de.danielbechler.diff.introspection

import spock.lang.Subject

class DefaultIntrospectorTest extends AbstractIntrospectorSpecification {

	@Subject
	DefaultIntrospector introspector = new DefaultIntrospector()

	def 'public field and accessors of the same name'() {
		given:
		introspector.returnFields = true

		when:
		def typeInfo = introspector.introspect(IntrospectorTestType)
		def accessors = typeInfo.accessors.grep {
			it.propertyName == 'publicFieldWithAccessors'
		}

		then: 'only one accessor with that name should be returned'
		accessors.size() == 1

		and: 'the getter-setter-accessor should win'
		accessors.first() instanceof PropertyAccessor
	}
}
