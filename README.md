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

* Generates a graph of your object, allowing you to build powerful visitors to extract and modify exactly what you want.
* Works with every kind of object, even those you can't modify.
* Can be configured entirely from the outside. No annotations needed. (Although they exist and make it much more convenient.)
* Properties can be grouped into categories, to easily compare or merge specific groups of properties.

## Note

Although I successfully use this framework in a rather complex project for quite a while now, I'm sure there are still many
edge cases, that I didn't even think about. Fortunately, the simple API makes it easy for you, to test it on your own set of
objects, to see if it works for you.

## Ways to Improve

* Performence has not been a hight priority so far, so there is still some room for improvement.
* Object comparison is very strict. Objects with different types cannot be compared, even when they share the same interface.
* It should be possible to apply the annotations to fields and not only to methods, to allow for some advanced merging techniques.
* It would be great to integrate a text-based diff algorithm for regular Strings.
* Documentation, documentation, documentation...

## Known Issues and

* Array handling is not implemented properly yet.
* Map keys are currently not compared via introspection and only used to identify map values.
