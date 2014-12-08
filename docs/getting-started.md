# Starter Guide

_java-object-diff_ provides a very simple API and tries to make everything as self-explanatory as possible. It can handle a wide variety of object structures without the need for any configuration. However, in more complex scenarios it is flexible enough to let you tailor it to your needs.

Now let's start with a very easy scenario: Comparing two maps.

```
Map<String, String> working = Collections.singletonMap("item", "foo");
Map<String, String> base = Collections.singletonMap("item", "bar");
DiffNode diff = ObjectDifferBuilder.buildDefault().compare(working, base);
```

Let's break down what we did here. We created a new `ObjectDiffer` and invoked its `compare` method, passing it two `java.util.Map` objects as arguments. The call returned a `DiffNode`. This represents the root of the object graph and contains other `DiffNode` instances, which represent the next level in the graph. In this case this means they represent the entries of the maps. Each node contains information about how the associated object has changed between the two given objects:

```
assert diff.hasChanges();
assert diff.childCount() == 1;
NodePath itemPath = NodePath.startBuilding().mapKey("item").build();
assert diff.getChild(itemPath).getState() == DiffNode.State.CHANGED;
```

In this case it's very simple. The map contains one entry with the same key and different values. So the root object (the map) contains changes. Since only one item has changed, the root node has one child node. That's the node representing the map entry with key `"item"`. And since its value differs between the base and the working object, `getState` returns the state `CHANGED`.

In this example we query the child node directly via `NodePath`, but in most cases you probably just want to iterate over all changes. Of course there is an easy way to do that, too. 

Let's say we want to print the path of each node along with its state:

```
diff.visit(new DiffNode.Visitor()
{
	public void node(DiffNode node, Visit visit)
	{
		System.out.println(node.getPath() + " => " + node.getState());
	}
});
```

The `DiffNode` provides a `visit` method which takes a `Visitor` as argument. This visitor will be called for every single node, including the root node itself. This way you don't need to know the structure of the objects your are diffing and can simply deal with the returned nodes how you see fit. This visitor pattern is very powerful when combined with the next feature: object accessors. 

Let's say we want to expand the example above by printing the actual values of the base and the working version. In order to do that, we need to extract those values from the input objects. Thankfully the `DiffNode` provides a method called `canonicalGet`, which knows exactly how to do that.

```
diff.visit(new DiffNode.Visitor()
{
	public void node(DiffNode node, Visit visit)
	{
		final Object baseValue = node.canonicalGet(base);
		final Object workingValue = node.canonicalGet(working);
		final String message = node.getPath() + " changed from " + 
							   baseValue + " to " + workingValue;
		System.out.println(message);
	}
});
```
This will generate the following output:

```
/ changed from {item=bar} to {item=foo}
/{item} changed from bar to foo
```

The output not only contains a line for the changed map entry, but also for the changed map itself. In order to avoid that, we could add a check for child nodes and only print nodes that don't contain any child nodes, as those are the ones that represent the actual change. Since that onle isn't very interesting, let's do it while looking at another feature: object write access.

So far we have created a nice diff, but you know what's cooler than diffing? Patching! Imagine we want to merge our changes into a new map. We can easily do that:

```
final Map<String, String> head = new HashMap<String, String>(base);
head.put("another", "map");
diff.visit(new DiffNode.Visitor()
{
	public void node(DiffNode node, Visit visit)
	{
		// only leave-nodes with changes
		if (node.hasChanges() && !node.hasChildren()) {
			node.canonicalSet(head, node.canonicalGet(working));
		}
	}
});

assert head.get("item").equals("foo");
assert head.get("another").equals("map");
```

The method `canonicalSet` provides write access to the value at the location of the node in the object graph, just as `canonicalGet` provides read access. There is a third method called `canonicalUnset`, which behaves differently based on the underlying object. It removes items from collections, nulls out objects and assigns default values to primitives. On top of that there are non-canonical versions of those methods, what act relative to their parent object. They can be useful in more advanced scenarios, but most of the time you'll probably not need them.

You now know how to create a diff, how to extract information from it and how to apply it as a patch. Congratulations, you are ready to try it out on your own objects. This example was pretty simple and we only worked on a simple `Map`. But don't let this foul you. Working with other objects isn't any different.

Of course there is much more to cover. Depending on your objects, there may be the need to do do some adjustments and tell _java-object-diff_ how certain values should be handled. There are tons of configuration options that allow you to tame almost any kind of object. If you run into any trouble, check the documentation for more information, as more and more features and frequently asked questions will get documented there. If that doesn't help, check out the [issue tracker](https://github.com/SQiShER/java-object-diff/issues) over at GitHub and ask for help.