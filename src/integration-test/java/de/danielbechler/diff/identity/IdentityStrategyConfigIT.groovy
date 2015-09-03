/*
 * Copyright 2015 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.identity
import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.comparison.IdentityStrategy
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.node.Visit
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.CollectionItemElementSelector
import de.danielbechler.diff.selector.MapKeyElementSelector
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import spock.lang.Specification

// TODO(SQiShER) Rewrite!
class IdentityStrategyConfigIT extends Specification {

	def working = new Container(
			productMap: [
					"PROD1": new Product(
							id: "PROD1",
							code: "Code1",
							productVersions: [
									new ProductVersion(id: "ID1", code: "PVC1"),
									new ProductVersion(id: "ID2", code: "PVC2")
							]),
					"PROD2": new Product(
							id: "PROD2",
							code: "Code2",
							productVersions: [
									new ProductVersion(id: "ID1", code: "PVC1"),
									new ProductVersion(id: "ID2", code: "PVC2")
							])
			],
			otherMap: [
					"PROD1": new Product(
							id: "PROD1",
							code: "Code1",
							productVersions: [
									new ProductVersion(id: "ID1", code: "PVC1"),
									new ProductVersion(id: "ID2", code: "PVC2")
							]),
					"PROD2": new Product(
							id: "PROD2",
							code: "Code2",
							productVersions: [
									new ProductVersion(id: "ID1", code: "PVC1"),
									new ProductVersion(id: "ID2", code: "PVC2")
							])
			]
	)

	def base = new Container(
			productMap: [
					"PROD1": new Product(
							id: "PROD1",
							code: "Code1",
							productVersions: [
									new ProductVersion(id: "ID3", code: "PVC1"),
									new ProductVersion(id: "ID4", code: "PVC2")
							]),
					"PROD2": new Product(
							id: "PROD2",
							code: "Code2",
							productVersions: [
									new ProductVersion(id: "ID3", code: "PVC1"),
									new ProductVersion(id: "ID4", code: "PVC2")
							])
			],
			otherMap: [
					"PROD1": new Product(
							id: "PROD1",
							code: "Code1",
							productVersions: [
									new ProductVersion(id: "ID1", code: "PVC1"),
									new ProductVersion(id: "ID2", code: "PVC2")
							]),
					"PROD2": new Product(
							id: "PROD2",
							code: "Code2",
							productVersions: [
									new ProductVersion(id: "ID1", code: "PVC1"),
									new ProductVersion(id: "ID2", code: "PVC2")
							])
			]
	)

	def 'Without IdentityStrategy'() {
		when:
		  def node = ObjectDifferBuilder
				  .startBuilding()
				  .filtering().returnNodesWithState(DiffNode.State.UNTOUCHED).and()
				  .build().compare(working, base);
		then: "High level nodes all changed"
		  // print(node, working, base)
		  node.getChild("otherMap").untouched
		  node.getChild("productMap").changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions").changed
		and: "ID1 and ID2 are ADDED"
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1Selector).added
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV2Selector).added
		and: "ID3 and ID4 are REMOVED"
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV3Selector).removed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV4Selector).removed
	}

	def 'PropertyOfType configuration WITH IdentityStrategy'() {
		when:
		  def node = ObjectDifferBuilder
				  .startBuilding()
				  .comparison().ofCollectionItems(Product, "productVersions").toUse(codeIdentity).and()
				  .filtering().returnNodesWithState(DiffNode.State.UNTOUCHED).and()
				  .build().compare(working, base);
		then: "High level nodes"
		  // print(node, working, base)
		  node.getChild("otherMap").untouched
		  node.getChild("productMap").changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions").changed
		and: "ID1 and ID2 are CHANGED"
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1CodeSelector).changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1CodeSelector).changed
		and: "id changed, code untouched"
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1CodeSelector).getChild("id").changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1CodeSelector).getChild("code").untouched
	}

	def 'OfNode configuration WITH IdentityStrategy'() {
		when:
		  def node = ObjectDifferBuilder
				  .startBuilding()
				  .comparison().ofCollectionItems(
				  // this is not very useful without wildcards on maps and collections...
				  NodePath.startBuilding().propertyName("productMap").mapKey("PROD1")
						  .propertyName("productVersions").build()
		  ).toUse(codeIdentity).and()
				  .filtering().returnNodesWithState(DiffNode.State.UNTOUCHED).and()
				  .build().compare(working, base);
		then: "High level nodes"
//            print(node, working, base)
		  node.getChild("otherMap").untouched
		  node.getChild("productMap").changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions").changed
		and: "ID1 and ID2 are CHANGED"
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1CodeSelector).changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1CodeSelector).changed
		and: "id changed, code untouched"
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1CodeSelector).getChild("id").changed
		  node.getChild("productMap").getChild(new MapKeyElementSelector("PROD1")).getChild("productVersions")
				  .getChild(PV1CodeSelector).getChild("code").untouched
	}


	private void print(final DiffNode diffNode, final Object working,
					   final Object base) {
		diffNode.visit(new DiffNode.Visitor() {
			@Override
			void node(final DiffNode node, final Visit visit) {
				System.out.println("" + node.getPath() + " " + node.getState()
						// + " " + node.canonicalGet(base) + " => " + node.canonicalGet(working)
				)
			}
		})
	}


	public static class Container {
		Map<String, Product> productMap;
		Map<String, Product> otherMap;
	}

	public static interface CodeId {
		String getCode();
	}

	@EqualsAndHashCode(includes = ["id"])
	@ToString(includePackage = false)
	public static class Product implements CodeId {
		String id;
		String code;
		List<ProductVersion> productVersions;
		List<ProductVersion> others;
	}

	@EqualsAndHashCode(includes = ["id"])
	@ToString(includePackage = false)
	public static class ProductVersion implements CodeId {
		String id;
		String code;
	}

	@EqualsAndHashCode(includes = ["id"])
	@ToString(includePackage = false)
	public static class OtherClass implements CodeId {
		String id;
		String code;
		List<ProductVersion> productVersions;
	}

	def codeIdentity = new IdentityStrategy() {
		@Override
		boolean equals(final Object working, final Object base) {
			return Objects.equals(((CodeId) working).getCode(), ((CodeId) base).getCode());
		}
	}

	def PV1Selector = new CollectionItemElementSelector(new ProductVersion(id: "ID1", code: "PVC1"));
	def PV2Selector = new CollectionItemElementSelector(new ProductVersion(id: "ID2", code: "PVC2"));
	def PV3Selector = new CollectionItemElementSelector(new ProductVersion(id: "ID3"));
	def PV4Selector = new CollectionItemElementSelector(new ProductVersion(id: "ID4"));

	// need to fill code as well because that's used for the codeIdentity cases
	def PV1CodeSelector = new CollectionItemElementSelector(new ProductVersion(code: "PVC1"));
	def PV2CodeSelector = new CollectionItemElementSelector(new ProductVersion(code: "PVC2"));
}
