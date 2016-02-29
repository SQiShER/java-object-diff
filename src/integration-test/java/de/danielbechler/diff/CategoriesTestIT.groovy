package de.danielbechler.diff

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

	def categoriesVisitor = new DiffNode.Visitor() {

		Map<String, Set<String>> mapCategories = new HashMap<>();

		@Override
		void node(DiffNode node, Visit visit) {

			mapCategories.put(node.getPropertyName(), node.getCategories())
		}
	}

	def "should return all categories"(){
		given:
			differ.compare(obj1,obj2).visitChildren(categoriesVisitor)
	   expect :
			categoriesVisitor.mapCategories.get("firstString") == ["cat1"] as Set
			categoriesVisitor.mapCategories.get("secondString") == ["cat1"] as Set
			categoriesVisitor.mapCategories.get("thirdString") == ["cat1","catAnnotation"] as Set
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

		def getFirstString() {
			return firstString
		}

		void setFirstString(firstString) {
			this.firstString = firstString
		}

		def getSecondString() {
			return secondString
		}

		void setSecondString(secondString) {
			this.secondString = secondString
		}

        @ObjectDiffProperty(categories = ["catAnnotation"])
		def getThirdString() {
			return thirdString
		}

		void setThirdString(thirdString) {
			this.thirdString = thirdString
		}
	}
}
