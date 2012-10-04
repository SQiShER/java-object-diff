## News

The latest version 0.10 is out and introduces some improvements, that affect the way objects are compared. Make sure to read the [changelogs](https://github.com/SQiShER/java-object-diff/wiki/Changelog) before you upgrade and let me know, if the changes are causing you any trouble.

## Introduction

Sometimes you need to figure out, how one version of an object differs from another one. One of the simplest solutions that'll cross your mind is most certainly to use reflection to scan the object for fields or getters and use them to compare the values of the different object instances. In many cases this is a perfectly valid strategy and the way to go. After all, we want to keep things simple, don't we?

However, there are some cases that can increase the complexity dramatically. What if you need to find differences in collections or maps? What if you have to deal with nested objects that also need to be compared on a per-property basis? Or even worse: what if you need to merge such objects?

You suddenly realize that you need to scan the objects recursively, figure out which collection items have been added, removed or changed; find a way to return your results in a way that allows you to easily access the information you are looking for and provide accessors to apply changes.

While all this isn't exactly rocket science, it is complex enough to add quite a lot of extra code to your project. Code that needs to be tested and maintained. Since the best code is the code you didn't write, this library aims to help you with all things related to diffing and merging of Java objects by providing a robust foundation and a simple, yet powerful API.

This library will hide all the complexities of deep object comparison behind one line of code:

	Node root = ObjectDifferFactory.getInstance().compare(workingObject, baseObject);

This generates a tree structure of the given object type and lets you traverse its nodes via visitors. Each node represents  one property (or collection item) of the underlying object and tells you exactly if and how the value differs from the base version. It also  provides accessors to read, write and remove the value from or to any given instance. This way, all you need to worry about is **how to treat** changes and **not how to find** them.

This library has been battle-tested in a rather big project of mine, where I use it to generate **activity streams**, resolve database **update conflics**, display **change logs** and limit the scope of entity updates to only a **subset of properties**, based on the context or user permissions. It didn't let me down so far and I hope that it can help you too!

## Getting Started

To learn how to use **Java Object Diff**, please have a look at the [Starter Guide](https://github.com/SQiShER/java-object-diff/wiki/Getting-Started).

## Features

* Generates a graph of your object, in which each node provides information about the changes and accessors to read and write the value on any instance of the given type.
* Visitor-support allows you to extract and modify exactly what you want.
* Designed to work with any kind of object out-of-the-box.
* Makes dealing with Collections and Maps very easy.
* Properties can be categorized, to easily compare or merge specific subsets. (This is particulary useful for databases like [MongoDB](http://www.mongodb.org/) that support [atomic property operations](http://www.mongodb.org/display/DOCS/Atomic+Operations).)
* Comparison can be improved and customized via annotations and/or configuration API.
* No annotations needed. (However, they exist for your convenience.)
* No runtime dependencies except for [SLF4J](http://www.slf4j.org/).

## Use Cases

**Java Object Diff** is currently used (but not limited) to...

* Generate Facebook-like activity streams
* Visualize the differences between object versions
* Automatically resolve conflicts on conflicting database updates
* Detect and persist only properties that were actually changed

## How to Improve

* Performance has not been a hight priority so far, so there is still some room for improvement.
* Object comparison is very strict. Objects with different types cannot be compared, even when they share the same interface.
* It should be possible to apply the annotations to fields and not only to methods, to allow for some advanced merging techniques.
* It would be great to integrate a text-based diff algorithm for regular strings.
* Some out-out-of-the-box Mergers would be nice (e.g. `LeftToRightMerger`, `NonConflictingMerger`, etc.)
* Needs more documentation and could use some more tests.

## Known Issues and Limitations

Please refer to the [Issue Tracker](https://github.com/SQiShER/java-object-diff/issues?state=open) for a list of currently known limitations.
