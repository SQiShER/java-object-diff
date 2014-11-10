# User Guide

This guide will give you a brief overview over the most important parts of the public API.

## ObjectDifferBuilder

The `ObjectDifferBuilder` is the entry point of every diffing operation. It acts as a factory to get hold of an actual `ObjectDiffer` instance and exposes a powerful configuration API in order to customize its behavior to suit your needs.

### Configuration API

The following sections describes the different parts of the configuration API.

#### ReturnableNodeConfiguration

Allows to exclude nodes from being added to the object graph based on criteria that are only known after the diff for the affected node and all its children has been determined.

Currently it is only possible to configure returnability based on the state (_added_, _changed_, _untouched_, etc.) of the `DiffNode`. But this is just the beginning. Nothing speaks against adding more powerful options. It would be nice for example to be able to pass some kind of matcher to determine returnability based on dynamic criteria at runtime.

#### IntrospectionConfiguration

Allows to replace the default bean introspector with a custom implementation. The default introspector internally uses the `java.beans.Introspector` which has some limitations. The most important one being that it only operates on getters and setters. In case field introspection is needed a custom introspector must be used. An introspector can be set as global default or on a per-property basis. It is also possible to turn off introspection for specific properties in which case they will simply be compared via `equals` method.

#### CircularReferenceConfiguration

Allows to define how the circular reference detector compares object instances. By default it uses the equality operator (`==`) which should be fine in mose cases. 

When dealing with object models that return copies of its properties on every access, it's possible to end up in infinite loops, because even though the objects may look the same, they would be different instances. In those cases it is possible to switch the instance detection mode to use the equals method instead of the equality operator. This way objects will be considered to be "the same" whenever `equals` returns `true`.

This configuration interface also allows to register a custom handler for exception thrown, whenever a circular reference is detected. The default handler simply logs a warning.

#### InclusionConfiguration

Allows to in- or exclude nodes based on property name, object type, category or location in the object graph. Excluded nodes will not be compared, to make sure their accessors won't get called. This is useful in cases where getters could throw exceptions under certain conditions or when certain accessors are expensive to call or simply not relevant for the use-case. 

In combination with categories this allows to define sub-sets of properties, in order to compare only relevant parts of an object (e.g. exclude all properties marked as _metadata_.)

#### ComparisonConfiguration

Allows to configure the way objects are compared. Sometimes introspection is just not the way to go. Let it be for performance reasons or simply because the object doesn't expose any useful properties. In those cases it's possible to define alternative comparison strategies, like using the equals method, a comparator or even a custom strategy. These settings can be made for specific nodes or entire types.

#### CategoryConfiguration

Allows to assign custom categories (or tags) to entire types or selected elements and properties. These categories come in very handy, when combined with the `InclusionConfiguration`. They make it very easy to limit the comparison to a specific subset of the object graph.

## ObjectDiffer

The `ObjectDiffer` is created by the `ObjectDifferBuilder`. It takes two objects to compare and eventually returns a `DiffNode` representing the entire object graph. It is thread-safe and can be reused. 

The objects given to the `ObjectDiffer` are called _working_ and _base_, where _working_ is considered to be a modified version of _base_. There is actually no technical reason for this. The `ObjectDiffer` doesn't care how the objects relate to each other. They could actually be two completely different objects that never shared a mutual history. The reason for the naming is simply to make it easier to express changes by using words like "added" and "removed".

## DiffNode

`DiffNodes` are used to build a directed graph representing every element (bean properties, collection items, map entries, etc.) of the compared objects. Each `DiffNode` can be queried for information on how the working version differs from the base.

Every `DiffNode` has exactly one parent and any number of child nodes. The only one that doesn't need or has a parent is the **root node**, which is the one returned by the `ObjectDiffer`. This node simply represents the compared objects themselve.

`DiffNodes` also provide methods to read and write the elements they represent from and to any object that resembles the type of the compared object. This is useful for reading the values from the working and base object to visualize changes or to merge non-conflicting changes on concurrent database updates.

Due to the fact that `DiffNodes` form a tree structure, each node has its own unique path. That allows it to easily collect selected information from the object graph. `DiffNodes` can be queried for their `NodePath` and many of the configuration APIs use it to attach metadata or inclusion rules to selected nodes.

Of course, it is not always possible or desirable to hard-code knowledge about the structure of the object graph into your software. In those cases it is also possible to use visitors to traverse the object graph and collect information about changes programmatically. But more about that in the next chapter.

## DiffNode.Visitor

The simplistic `Visitor` inteface allows to implement very powerful logic to transform the `DiffNode` graph into any representation needed. By passing a visitor to the `visit` or `visitChildren` method of a `DiffNode`, one can decide whether it should be exclusively applied to the single node or also recursively to all its children.

Every encountered `DiffNode` will be passed to the `Visitor` along with a `Visit` object. The diff information and the accessor methods of the node allow the visitor to query and change the values of the base and working object (or any other instance of the same object type) - as long as the visitor has access to those objects. Once the visitor has fulfilled its purpose it can either stop the traversal altogether or just prevent it for the children of the current node by calling `stop()` or `dontGoDeeper()` on the `Visit` object.

_java-object-diff_ comes with a bunch of visitors that do a variety of things. Be it simply printing the differences between base and working; locating nodes with a specific category or even merging the differences between diffed versions into an even newer head version. However, these visitors merely serve as examples since probably none of them will fit your use-case as good as your own custom implementation could.

It is **highly encouraged** to do all the processing of the object graph via `Visitors` as it is the most flexible, reliable and maintainable way.

## NodePath

Every `DiffNode` in the object graph has its unique path identifier - simply called the `NodePath`. Such a path is composed by a sequence of `Elements`. Each element describes how to transition from one object to the next. For example by getting an entry from a map or an item from a list or just by calling a simple property getter.

A sequence of these actions describes one exact location in the object graph and therefore serves as the single best way to query specific nodes. There is also one special element that every NodePath needs to start with: the root element. It is used to reference the objects that have been passed to the compare method of the `ObjectDiffer`.

A `NodePath` can be created by calling one of its numerous static builder methods.

# Conclusion

Hopefully this brief overview over the most important classes and concepts of java-object-diff helps you getting the best out of this library. If you think something important is missing or if anything isn't as well explained as you'd hoped, please let me know!