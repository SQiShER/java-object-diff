package de.danielbechler.diff.categories

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.introspection.ObjectDiffProperty
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.node.Visit
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

class CategoriesTestIT extends Specification{

	def obj1 = new MyObject("aaa","aaa", "aaa")
	def obj2 =  new MyObject("bbb","bbb", "bbb")
	def differ = ObjectDifferBuilder.startBuilding()
			.categories()
			.ofNode(NodePath.with("firstString")).toBe("cat1")
			.ofNode(NodePath.with("secondString")).toBe("cat1")
			.ofNode(NodePath.with("thirdString")).toBe("cat1")
			.and()
			.build()

	def "should return all categories"(){
		given:
			def node = differ.compare(obj1,obj2)
		expect :
			node.getChild("firstString").getCategories() == ["cat1"] as Set
			node.getChild("secondString").getCategories() == ["cat1"] as Set
			node.getChild("thirdString").getCategories() == ["cat1", "catAnnotation"] as Set
	}

	class MyObject{

		def firstString
        def secondString
		def thirdString

		MyObject(firstString,secondString,thirdString) {

			this.firstString = firstString
			this.secondString = secondString
			this.thirdString = thirdString
		}

        @ObjectDiffProperty(categories = ["catAnnotation"])
		def getThirdString() {
			return thirdString
		}
	}
}
