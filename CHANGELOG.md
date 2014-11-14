# Changelog

## 0.91.1

### Features
- **Exception Handling:** Reimplemented property access exception handling

## 0.91

### Features
- **Inclusion:** Implemented a mechanism to control the inclusion of properties of specific object types.
- **Inclusion:** Implemented a mechanism to register custom inclusion resolvers to support inclusion rules far beyond the built-in ones.
- **Introspection:** Implemented a mechanism to register a custom instance factory, which is used by `DiffNode#canonicalSet` in order to create instances of missing objects 

### Improvements
- **Inclusion:** Performance improvements (Thanks [@Deipher](https://github.com/Deipher))
- **Inclusion:** `InclusionService` has been split into several `InclusionResolver`s
- **DiffNode:** `canonicalSet` now automatically creates missing objects along the path to the root object 

### Bugfixes
- **Circular Reference Detection:** Fixed 'Detected inconsistency in enter/leave sequence. Must always be LIFO.' bug that could occur due to inconsistent cleanup of the instance memory when a circular reference has been detected.

## TODO: Migrate missing Github release notes

## 0.12 (01-Jul-2013)

### Features

* [[#66](https://github.com/SQiShER/java-object-diff/issues/66)] The signature of compared objects is now determined at runtime whenever possible. The signature of bean property values used to simply be the one of its getters return type. The object differ now does its best to determine the most specific **shared** object type. It does this like so:

	* If the object types of base and working are the same, the signature of this type will be used for introspection. (1)
	* If the object types of base and working are different, the signature of their closest shared superclass will be used. (2)
	* If no shared superclass could be found, it falls back the the declared return type of the objects property getter. (3)
	
	The runtime type analysis will not consider shared interfaces, so the only way to diff exclusively against interface signatures, is to cause the type lookup to fall through to (3). Currently I'm not sure if it needs to be possible to force the object differ to always use the declared type. If you think it does or this new behavior causes you any trouble, please let me know.


## 0.11.1 (10-May-2013)

### Bug Fixes

- Fixed Java 5 incompatibility (replaced `Deque` with `LinkedList`)

### Improvements

- Added Maven Plugin to verify Java 5 API compatibility

## 0.11 (27-Feb-2013)

### Features

- Added a method to `PropertyNode` to return all annotations of its accessor [[#46](https://github.com/SQiShER/java-object-diff/issues/46)]
- Circular nodes now provide access to the node where the circle started [[#52](https://github.com/SQiShER/java-object-diff/issues/52)]
- Allowed to configure the way circular references are detected. Objects can now either be matched as usual via equality operator or alternatively via equals method

### Improvements

- Added an [example](https://github.com/SQiShER/java-object-diff/blob/master/src/main/java/de/danielbechler/diff/example/CanonicalAccessorExample.java) to demonstrate the difference between `Node#get(Object)` and `Node#canonicalGet(Object)`
- Lowered minimum required Java version from Java 6 to 5 [[#51](https://github.com/SQiShER/java-object-diff/issues/51)]
- The library is now packaged in an OSGi-compliant way [[#53](https://github.com/SQiShER/java-object-diff/issues/53)]

## 0.10.2 (11-Dec-2012)

### Bug Fixes

- Fixed a [bug](https://github.com/SQiShER/java-object-diff/issues/43) that caused `Maps` and `Collections` to be improperly compared in case they were configured to use the equals only comparison strategy

### Improvements

- Major refactoring of most core classes to allow for better extendability and testability
- Increased test coverage
- Added some more code examples
- Upgraded to latest versions of SLF4J, Mockito and TestNG

## 0.10.1 (10-Oct-2012)

### Bug Fixes

- Ignored properties will no longer be accessed (regression)

### Improvements

- Starting from this release `java-object-diff` will be available via Maven Central repository

## Version 0.10 (04-Oct-2012)

### Bug Fixes

- `logback.xml` is no longer included in the JAR file

### Features

- It is now possible to retrieve the property graph of added and removed nodes
- Collections and Maps of different types can now be properly compared
- Added configuration option to allow primitive default values to be treated like `null` objects or regular values

### Improvements

- Switched from JUnit to TestNG
- Accessors now have a neat toString representation
- Changed output format of printing visitors a little bit
- The `ADDED` and `REMOVED` state of object nodes is now simply `null` based (default values won't be taken into account anymore)
- Private constructors can now be accessed to determine the default values of primitive properties
- Primitive types are now handled in a more intuitive way