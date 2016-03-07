package de.danielbechler.diff.categories

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.introspection.ObjectDiffProperty
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

class CategoriesTestIT extends Specification {

	def "should return all categories"() {
		setup:
		  def obj1 = new MyObject("aaa", "aaa")
		  def obj2 = new MyObject("bbb", "bbb")
		  def differ = ObjectDifferBuilder.startBuilding()
				  .categories()
				  .ofNode(NodePath.with("firstString")).toBe("cat1")
				  .ofNode(NodePath.with("secondString")).toBe("cat1")
				  .and()
				  .build()
		  def node = differ.compare(obj1, obj2)

		expect:
		  node.getChild("firstString").getCategories() == ["cat1"] as Set
		  node.getChild("secondString").getCategories() == ["cat1", "catAnnotation"] as Set
	}

	@SuppressWarnings("GroovyUnusedDeclaration")
	class MyObject {
		def firstString
		def secondString

		MyObject(firstString, secondString) {
			this.firstString = firstString
			this.secondString = secondString
		}

		@ObjectDiffProperty(categories = ["catAnnotation"])
		def getSecondString() {
			return secondString
		}

		void setSecondString(secondString) {
			this.secondString = secondString
		}
	}
}
