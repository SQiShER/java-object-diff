**This article is a little outdated, as it refers to the versions prior to 0.90. The most important change is that the `ObjectDifferFactory` doesn't exist anymore and has been replaced with a more flexible `ObjectDifferBuilder`. I'll try to update this post as soon as I can. Until then please refer to the [Integration Tests](https://github.com/SQiShER/java-object-diff/tree/master/src/integration-test/java/de/danielbechler/diff) for some working examples.**

***

Before we get started, you need to [add the dependency](https://github.com/SQiShER/java-object-diff/wiki/Maven) to your projects POM.

## First Steps

Now that you have the framework at hand, let’s have a look at the API. The most important class you need to know about is the `ObjectDifferFactory`. This is the one and only way to create new `ObjectDiffer` instances.

```java
ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
```

Great, so there we have our `ObjectDiffer`. Now what? Let’s see what we can do with it! The only method you need to care about right now is `<T> Node compare(T, T)`. It will do all magic and return a root node, representing the objects, you passed as arguments. Lets test it out with simple `String`s.

```java
final String working = "Hello";
final String base = "World";
final Node root = objectDiffer.compare(working, base);
```

As you can see, we are thinking in terms of a working (or potentially modified) and its corresponding base version. Terms like `ADDED` or `REMOVED` will always relate to the working version.

So how can we see, if the above code returns the expected result? It would be nice to simply print the entire `Node` hierarchy in a readable form. Fortunately, there is a `Visitor` for this:

```java
root.visit(new PrintingVisitor(working, base));
```

This will print the following output to the console:

```
Property at path '/' has been changed from [ World ] to [ Hello ]
```

That’s great! It works just as expected. Unfortunately this example is pretty boring, considering that you could have gotten to the same result using the `equals` method of one of the Strings. So lets move on to a more complicated example.

## Advanced Example

The following example uses classes from the [test package](https://github.com/SQiShER/java-object-diff/tree/master/src/test/java/de/danielbechler/diff/integration), in case you want to see their implementation details.

Let us start with setting up a simple phone book:

```java
final PhoneBook phoneBook = new PhoneBook("Breaking Bad");
```

Now we add some contacts.

```java
final Contact walterWhite = new Contact("Walter", "White");
walterWhite.setPhoneNumber("Home", new PhoneNumber("1", "505", "316-7871"));
walterWhite.setPhoneNumber("Work", new PhoneNumber("1", "505", "456-3788"));
phoneBook.addContact(walterWhite);

final Contact jessePinkman = new Contact("Jesse", "Pinkman");
jessePinkman.setPhoneNumber("Home", new PhoneNumber("1", "505", "234-4628"));
phoneBook.addContact(jessePinkman);
```

In order to make some changes, we create a copy of the original phone book.

```java
final PhoneBook modifiedPhoneBook = PhoneBook.from(phoneBook);
```

The `from` method is a simple cloning factory. Now lets add middle names to our contacts:

```java
modifiedPhoneBook.getContact("Jesse", "Pinkman").setMiddleName("Bruce");
modifiedPhoneBook.getContact("Walter", "White").setMiddleName("Hartwell");
```

The setup is complete. Time to fire up the `ObjectDiffer`:

```java
final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
final Node root = objectDiffer.compare(modifiedPhoneBook, phoneBook);
```

To visualize the changes, we use the `PrintingVisitor` again.

```java
final Node.Visitor visitor = new PrintingVisitor(modifiedPhoneBook, phoneBook);
root.visit(visitor);
```

And it prints the expected result:

	Property at path '/contacts/item[Walter White]/middleName' has been added => [ Hartwell ]
	Property at path '/contacts/item[Jesse Pinkman]/middleName' has been added => [ Bruce ]

As you can see, the `ObjectDiffer` can handle any kind of object regardless of its complexity. Of course there is much more to it, than just printing the changes. In order to unleash the full power of this framework, we need to take a look at the `Visitor` interface.

## Visitors

The `Node` interface provides the `void visit(Visitor)` method. As unremarkable as it looks, it is the most powerful tool, in order to build impressive programs like activity stream generators or automatic conflict resolvers. The `Visitor` interface looks like this:

```java
public interface Visitor
{
	void accept(Node difference, Visit visit);
}
```

Once you invoke the `visit` method on any node, it will traverse the whole node graph and pass the visitor to all its children, which will do the same, until every `Node` has been visited. But what exactly can we do with a node?

A node contains everything you need, in order to decide how you want to treat it. It contains a state, which indicated whether the underlying property has been changed, removed, added, etc. It also provides accessors to `get`, `set` and `unset` the value on any object instance of the underlying type. And, of course, it knows its parent and child nodes.

## Conclusion

As you can see, the API really is very simple and getting started is a easy as adding a Maven dependency. However, the tree structure in combination with the visitor pattern allows for some very sophisticated uses. For more examples, please check out the [unit tests](https://github.com/SQiShER/java-object-diff/tree/master/src/test/java/de/danielbechler/diff).