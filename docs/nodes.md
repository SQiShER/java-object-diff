Nodes represent a part of an object. This could be the object itself, one of its properties, an item in a collection or a map entry. A node may have one parent node and any number of children. It also provides methods to read and write the property it represented on any object of the same type as the original object. Last but not least, a node knows how the associated property has changed compared to the base object. It is a universal interface to query for information and apply changes.

For example, let's say you have a Person class:

```java
public class Person
{
	private String firstName;
	private String lastName;

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}
}
```

If you compare two instances of this class like so:

```java
final Person bruceWayne = new Person("Bruce", "Wayne");
final Person batman = new Person("Batman", null);
final Node rootNode = ObjectDifferFactory.getInstance().compare(batman, bruceWayne);
```

The resulting node structure could be visualized somehow like this:

```
/ = { changed, type is de.danielbechler.diff.example.SimpleNodeExample.Person, 2 children }
    /firstName = { changed, type is java.lang.String, no children }
    /lastName = { removed, type is java.lang.String, no children }
```

In this example, the hierarchy is expressed by the indentation level. The root node `/` contains the two children `/firstName` and `/lastName`, which don't contain any more children. In a more complex example, this hierarchy could contain many more levels. In other words, nodes are nothing else but a different representation of the input class. They allow for simple traversal via visitors and can be represented and retrieved with a `PropertyPath`.