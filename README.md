# Java Object Diff

An easy-to-use Framework to detect and handle differences between different versions of a given Java Object.

## Use Cases

This Framework is currently used to…

* Generate Facebook-like activity streams
* Visualize the differences between two object versions
* Automatically resolve conflicts on concurrent database writes
* Detect and persist only properties that actually changed

None one of these solutions comes out-of-the-box, though. But this Framework was the solid foundation, that made them very easy to realize.

## Feature List

* Generates a graph of your object, allowing you to build powerful visitors to suit your needs.
* Works with every kind of object, so no need to change a thing.
* Can be configured entirely from the outside. No annotations needed. (Although they make it much more convenient.)
* No dependencies except for SLF4J.

## Disclaimer

Although I use this Framework in a rather complex project of mine, I'm sure there are still lots of corner cases, that I didn't discover myself. Fortunately, the simple API makes it very easy for you, to quickly test it out on your own set of objects.

## Known Issues and Ways to Improve

* Performance optimization was not the biggest concern so far, so there is still some room for improvement.
* Object comparison is very strict. Objects with different types cannot be compared, even when they share the same interface.
* Arrays are treated poorly
* The annotations should be applied to fields instead of methods. (Would make it easier to handle Arrays.)