# Java Object Diff

An easy-to-use Framework to find and handle differences between Java objects.

## Getting Started

To learn how to use **Java Object Diff**, please have a look at the short [starter guide](https://github.com/SQiShER/java-object-diff/wiki/Getting-Started).

## Features

* Generates a graph of your object, in which each node provides information about the changes and accessors to read and write the value on any instance of the given type.
* Visitor-support allows you to extract and modify exactly what you want.
* Designed to work with any kind of object out-of-the-box.
* Properties can be categorized, to easily compare or merge specific subsets. (This is very useful for databases like [MongoDB](http://www.mongodb.org/) that support [atomic property operations](http://www.mongodb.org/display/DOCS/Atomic+Operations).)
* Comparison can be improved and customized via Annotations or configuration API.
* No Annotations needed. (However, they exist for your convenience.)
* No runtime dependencies except for [SLF4J](http://www.slf4j.org/).

## Use Cases

**Java Object Diff** is currently used to...

* Generate Facebook-like activity streams
* Visualize the differences between object versions
* Automatically resolve conflicts on conflicting database updates
* Detect and persist only properties that were actually changed

Even though none of the above solutions comes out-of-the-box, this framework made them very easy to do.

## Note

I use this framework in a pretty big project for quite a while now, but I'm sure there are still many
edge cases, that I didn't run into. Fortunately, the simple API makes it easy for you, to test it on your
own set of objects, to see if it works for you. If you run into problems please contact me or open an issue
in the [issue tracker](https://github.com/SQiShER/java-object-diff/issues).

## How to Improve

* Performance has not been a hight priority so far, so there is still some room for improvement.
* Object comparison is very strict. Objects with different types cannot be compared, even when they share the same interface.
* It should be possible to apply the annotations to fields and not only to methods, to allow for some advanced merging techniques.
* It would be great to integrate a text-based diff algorithm for regular strings.
* Some out-out-of-the-box Mergers would be nice (e.g. `LeftToRightMerger`, `NonConflictingMerger`, etc.)
* Needs more documentation and could use some more tests.

## Known Issues and Limitations

* Array handling is not implemented properly yet.
* Map keys are currently not compared via introspection and only used to identify map values.
