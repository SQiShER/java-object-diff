## Introduction

`java-object-diff` is a simple, yet powerful library to find differences between Java objects. It takes two objects and generates a tree structure that represents any differences between the objects and their children. This tree can then be traversed to extract more information or apply changes to the underlying data structures.

[![Build Status](https://travis-ci.org/SQiShER/java-object-diff.svg?branch=master)](https://travis-ci.org/SQiShER/java-object-diff) [ ![Download](https://api.bintray.com/packages/sqisher/maven/java-object-diff/images/download.svg) ](https://bintray.com/sqisher/maven/java-object-diff/_latestVersion)

## Features

* Generates an easily traversable tree structure to analyze and modify with surgical precision
* Detects whether a value or item has been added, removed or changed and shows the changes
* Allows to manipulate the underlying objects directly through the generated tree nodes
* Works with almost any kind of object (Beans, Lists, Maps, Primitives, Strings, etc.)
* Properties can be marked with category tags to easily filter for specific subsets
* No configuration needed (but possible)
* No runtime dependencies except for [SLF4J](http://www.slf4j.org/)
* When needed, it leaves it up to you, whether you want to use declarative configuration or annotations

## Quickstart

### Maven

```xml
<dependency>
    <groupId>de.danielbechler</groupId>
    <artifactId>java-object-diff</artifactId>
    <version>0.13.1</version>
</dependency>
```

### Gradle

```groovy
compile 'de.danielbechler:java-object-diff:0.13.1'
```

## Getting Started

To learn how to use **Java Object Diff**, please have a look at the [Starter Guide](https://github.com/SQiShER/java-object-diff/wiki/Getting-Started).

## Why would you need this?

Sometimes you need to figure out, how one version of an object differs from another one. One of the simplest solutions that'll cross your mind is most certainly to use reflection to scan the object for fields or getters and use them to compare the values of the different object instances. In many cases this is a perfectly valid strategy and the way to go. After all, we want to keep things simple, don't we?

However, there are some cases that can increase the complexity dramatically. What if you need to find differences in collections or maps? What if you have to deal with nested objects that also need to be compared on a per-property basis? Or even worse: what if you need to merge such objects?

You suddenly realize that you need to scan the objects recursively, figure out which collection items have been added, removed or changed; find a way to return your results in a way that allows you to easily access the information you are looking for and provide accessors to apply changes.

While all this isn't exactly rocket science, it is complex enough to add quite a lot of extra code to your project. Code that needs to be tested and maintained. Since the best code is the code you didn't write, this library aims to help you with all things related to diffing and merging of Java objects by providing a robust foundation and a simple, yet powerful API.

This library will hide all the complexities of deep object comparison behind one line of code:

```java
Node root = ObjectDifferFactory.getInstance().compare(workingObject, baseObject);
```

This generates a tree structure of the given object type and lets you traverse its nodes via visitors. Each node represents  one property (or collection item) of the underlying object and tells you exactly if and how the value differs from the base version. It also  provides accessors to read, write and remove the value from or to any given instance. This way, all you need to worry about is **how to treat** changes and **not how to find** them.

This library has been battle-tested in a rather big project of mine, where I use it to generate **activity streams**, resolve database **update conflics**, display **change logs** and limit the scope of entity updates to only a **subset of properties**, based on the context or user permissions. It didn't let me down so far and I hope that it can help you too!

## Use Cases

**Java Object Diff** is currently used (but not limited) to...

* Generate Facebook-like activity streams
* Visualize the differences between object versions
* Automatically resolve conflicts on concurrent database updates
* Detect and persist only properties that were actually changed

## Contribute

You discovered a bug or have an idea for a new feature? Great, why don't you send me a [Pull 
Request (PR)](https://help.github.com/articles/using-pull-requests) so everyone can benefit from it? To help you getting started, [here](https://github.com/SQiShER/java-object-diff/blob/master/CONTRIBUTING.md) is a brief guide with everyting you need to know to get involved!

## Donate

If you’d like to support this project with a small donation, you can do so via  [Flattr](https://flattr.com/submit/auto?user_id=SQiShER&url=https://github.com/SQiShER/java-object-diff&title=java-object-diff&language=&tags=github&category=software) or [Bitcoin](https://blockchain.info/address/19kRmHJ4qMnYCY6rnY6kCf96Prj6WGxisk).

Alternatively you could send me a [nice tweet](https://twitter.com/SQiShER), start contributing, write a blog post about this project, tell your friends about it or simply star [this repository](https://github.com/SQiShER/java-object-diff). I'm happy about everything that shows me that someone out there is actually using this library and appreciates all the hard work that goes into its development.

[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=SQiShER&url=https://github.com/SQiShER/java-object-diff&title=java-object-diff&language=&tags=github&category=software)
