# Java Object Diff

An easy-to-use Framework to find and handle differences between Java objects.

## Getting Started

To learn how to use **Java Object Diff**, please have a look at the short [starter guide](https://github.com/SQiShER/java-object-diff/wiki/Getting-Started).

## Use Cases

**Java Object Diff** is currently used to…

* Generate Facebook-like activity streams
* Visualize the differences between two object versions
* Automatically resolve conflicts on concurrent database writes
* Detect and persist only properties that actually changed

Even though none one of the above solutions comes out-of-the-box, this framework made them very easy to realize.

## Feature List

* Generates a graph of your object, allowing you to build powerful visitors to suit your needs.
* Works with every kind of object, so no need to change a thing.
* Can be configured entirely from the outside. No annotations needed. (Although they exist and make it more convenient.)
* Properties can be grouped into categories, to easily compare specific groups of properties.
* No dependencies except for SLF4J.

## Note

Although I successfully use this framework in a rather complex project for quite a while now, I'm sure there are still many
edge cases, that I didn't even think about. Fortunately, the simple API makes it easy for you, to test it on your own set of
objects, to see if it works for you.

## Ways to Improve

* Performance optimization was not the biggest concern so far, so there is still some room for improvement.
* Object comparison is very strict. Objects with different types cannot be compared, even when they share the same interface.
* The annotations should be applied to fields instead of methods. (Would make it easier to handle Arrays.)
* It would be great to integrate a text-based diff algorithm for regular Strings.
* Documentation, documentation, documentation...

## Known Issues and

* Arrays are treated poorly (Actually not at all. Yet.)
* Objects serving as map keys should be compared as well. Currently their only purpose is to identify map values.
