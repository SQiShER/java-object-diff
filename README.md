## Introduction

`java-object-diff` is a simple, yet powerful library to find differences between Java objects. It takes two objects and generates a tree structure that represents any differences between the objects and their children. This tree can then be traversed to extract more information or apply changes to the underlying data structures.

[![Build Status](https://travis-ci.org/SQiShER/java-object-diff.svg?branch=master)](https://travis-ci.org/SQiShER/java-object-diff) 
[![Coverage Status](https://coveralls.io/repos/SQiShER/java-object-diff/badge.svg?branch=master&service=github)](https://coveralls.io/github/SQiShER/java-object-diff?branch=master)
[![Download](https://api.bintray.com/packages/sqisher/maven/java-object-diff/images/download.svg)](https://bintray.com/sqisher/maven/java-object-diff/_latestVersion)
[![Documentation Status](https://readthedocs.org/projects/java-object-diff/badge/?version=latest)](https://readthedocs.org/projects/java-object-diff/?badge=latest)

## Features

* Works out-of-the-box with with almost any kind of object and arbitrarily deep nesting
* Finds the differences between two objects
* Returns the differences in shape of an easily traversable tree structure
* Tells you everything there is to know about the detected changes
* Provides read and write access to the underlying objects, allowing you not only to extract the changed values but even to apply the diff as a patch
* Requires no changes to your existing classes (in most cases)
* Provides a very flexible configuration API to tailor everything to your needs
* Tiny, straightforward, yet very powerful API
* Detects and handles circular references in the object graph
* No runtime dependencies except for [SLF4J](http://www.slf4j.org/)
* Compatible with Java 1.5 and above

## Support this Project

If you like this project, there are a few things you can do to show your support:

* [**Follow me** on Twitter (@SQiShER)](https://twitter.com/SQiShER)
* [**Surprise me** with something from my Amazon Wishlist](http://www.amazon.de/registry/wishlist/2JFW27V71CBGM)
* [**Contribute** code, documentation, ideas, or insights into your use-case](https://github.com/SQiShER/java-object-diff/blob/master/CONTRIBUTING.md)
* Star this repository (stars make me very happy!)
* Talk about it, write about it, recommend it to others

But most importantly: **don't ever hesitate to ask me for help**, if you're having trouble getting this library to work. The only way to make it better is by hearing about your use-cases and pushing the limits!

## Getting Started

To learn how to use **Java Object Diff** have a look at the [Getting Started Guide](http://java-object-diff.readthedocs.org/en/latest/getting-started/).

### Using with Maven

```xml
<dependency>
    <groupId>de.danielbechler</groupId>
    <artifactId>java-object-diff</artifactId>
    <version>0.95</version>
</dependency>
```

### Using with Gradle

```groovy
compile 'de.danielbechler:java-object-diff:0.95'
```

## Documentation

The documentation can be found over at [ReadTheDocs](http://java-object-diff.readthedocs.org/en/latest/).

## Caveats

* Introspection of values other than primitives and collection types is curently done via standard JavaBean introspection, which requires your objects to provide getters and setters for their properties. However, you don't need to provide setters, if you don't need write access to the properties (e.g. you don't want to apply the diff as a patch.)

	If this does not work for you, don't worry: you can easily write your own introspectors and just plug them in via configuration API.

* Ordered lists are currently not properly supported (they are just treated as Sets). While this is something I definitely want to add before version `1.0` comes out, its a pretty big task and will be very time consuming. So far there have been quite a few people who needed this feature, but not as many as I imagined. So your call to action: if you need to diff and/or merge collection types like `ArrayList`, perhaps even with multiple occurence of the same value, please let me know. The more I'm aware of the demand and about the use-cases, the more likely it is, that I start working on it.

## Why would you need this?

Sometimes you need to figure out, how one version of an object differs from another one. One of the simplest solutions that'll cross your mind is most certainly to use reflection to scan the object for fields or getters and use them to compare the values of the different object instances. In many cases this is a perfectly valid strategy and the way to go. After all, we want to keep things simple, don't we?

However, there are some cases that can increase the complexity dramatically. What if you need to find differences in collections or maps? What if you have to deal with nested objects that also need to be compared on a per-property basis? Or even worse: what if you need to merge such objects?

You suddenly realize that you need to scan the objects recursively, figure out which collection items have been added, removed or changed; find a way to return your results in a way that allows you to easily access the information you are looking for and provide accessors to apply changes.

While all this isn't exactly rocket science, it is complex enough to add quite a lot of extra code to your project. Code that needs to be tested and maintained. Since the best code is the code you didn't write, this library aims to help you with all things related to diffing and merging of Java objects by providing a robust foundation and a simple, yet powerful API.

This library will hide all the complexities of deep object comparison behind one line of code:

```java
DiffNode root = ObjectDifferBuilder.buildDefault().compare(workingObject, baseObject);
```

This generates a tree structure of the given object type and lets you traverse its nodes via visitors. Each node represents  one property (or collection item) of the underlying object and tells you exactly if and how the value differs from the base version. It also  provides accessors to read, write and remove the value from or to any given instance. This way, all you need to worry about is **how to treat** changes and **not how to find** them.

This library has been battle-tested in a rather big project of mine, where I use it to generate **activity streams**, resolve database **update conflics**, display **change logs** and limit the scope of entity updates to only a **subset of properties**, based on the context or user permissions. It didn't let me down so far and I hope it can help you too!

## Contribute

You discovered a bug or have an idea for a new feature? Great, why don't you send me a [Pull 
Request](https://help.github.com/articles/using-pull-requests) so everyone can benefit from it? To help you getting started, [here](https://github.com/SQiShER/java-object-diff/blob/master/CONTRIBUTING.md) is a brief guide with everyting you need to know to get involved!

---

Thanks to JetBrains for supporting this project with a free open source license for their amazing IDE **IntelliJ IDEA**.

[![IntelliJ IDEA](https://www.jetbrains.com/idea/docs/logo_intellij_idea.png)](https://www.jetbrains.com/idea/)
