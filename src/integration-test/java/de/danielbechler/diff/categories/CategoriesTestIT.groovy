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

	def node = differ.compare(obj1,obj2)

	def categoriesMapVisitor = new DiffNode.Visitor() {

		Map<String, Set<String>> mapCategories = new HashMap<>();

		@Override
		void node(DiffNode node, Visit visit) {

			mapCategories.put(node.getPropertyName(), node.getCategories())
		}
	}

	def categoriesAdderVisitor = new DiffNode.Visitor() {

		@Override
		void node(DiffNode node, Visit visit) {

			node.addCategories(Arrays.asList("addedWhileVisiting"))
		}
	}

	def "should return all categories"(){
		given:
			node.visitChildren(categoriesMapVisitor)
	   expect :
			categoriesMapVisitor.mapCategories.get("firstString") == ["cat1"] as Set
			categoriesMapVisitor.mapCategories.get("secondString") == ["cat1"] as Set
			categoriesMapVisitor.mapCategories.get("thirdString") == ["cat1", "catAnnotation"] as Set
	}

	def "should return categories added when visiting"(){
		given:
			node.visitChildren(categoriesAdderVisitor)
			node.visitChildren(categoriesMapVisitor)
		expect :
			categoriesMapVisitor.mapCategories.get("firstString") == ["cat1", "addedWhileVisiting"] as Set
			categoriesMapVisitor.mapCategories.get("secondString") == ["cat1", "addedWhileVisiting"] as Set
			categoriesMapVisitor.mapCategories.get("thirdString") == ["cat1", "catAnnotation", "addedWhileVisiting"] as Set
	}

	def "categories should not be modifiable by a client directly"(){

		when:
			node.visitChildren(new DiffNode.Visitor() {

				@Override
				void node(DiffNode node, Visit visit) {

					def cats = node.getCategories()
					cats.removeAll()
				}
			})

		then :
			thrown UnsupportedOperationException
	}

	def "should throw exception when added a null collection"(){

		when:
		node.visitChildren(new DiffNode.Visitor() {
			@Override
			void node(DiffNode node, Visit visit) {

				node.addCategories(null)
			}
		})

		then :
			def ex = thrown(IllegalArgumentException)
			ex.message == "'additionalCategories' must not be null"
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
