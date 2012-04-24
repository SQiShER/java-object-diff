package de.danielbechler.diff.integration;

import de.danielbechler.diff.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

/** @author Daniel Bechler */
public class PhoneBookTest
{
	@Test
	public void test()
	{
		final PhoneBook phoneBook = new PhoneBook("Breaking Bad");

		final Contact walterWhite = new Contact("Walter", "White");
		walterWhite.setPhoneNumber("Home", new PhoneNumber("1", "505", "316-7871"));
		walterWhite.setPhoneNumber("Work", new PhoneNumber("1", "505", "456-3788"));
		phoneBook.addContact(walterWhite);

		final Contact jessePinkman = new Contact("Jesse", "Pinkman");
		jessePinkman.setPhoneNumber("Home", new PhoneNumber("1", "505", "234-4628"));
		phoneBook.addContact(jessePinkman);

		final PhoneBook modifiedPhoneBook = PhoneBook.from(phoneBook);
		modifiedPhoneBook.getContact("Jesse", "Pinkman").setMiddleName("Bruce");
		modifiedPhoneBook.getContact("Walter", "White").setMiddleName("Hartwell");

		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();
		final Node node = objectDiffer.compare(modifiedPhoneBook, phoneBook);

		assertThat(node.hasChanges(), is(true));
		assertThat(node.hasChildren(), is(true));
		assertThat(node.getChildren().size(), is(1));

		final Node contactsNode = node.getChild("contacts");
		assertThat(contactsNode, IsNull.notNullValue());
		assertThat(contactsNode.hasChanges(), is(true));

		final Node pinkmanNode = contactsNode.getChild(new CollectionElement(jessePinkman));
		assertThat(pinkmanNode.hasChanges(), is(true));

		final Node middleNameNode = pinkmanNode.getChild("middleName");
		assertThat(middleNameNode.hasChanges(), is(true));
		assertThat(middleNameNode.canonicalGet(phoneBook), IsNull.nullValue());
		assertThat((String) middleNameNode.canonicalGet(modifiedPhoneBook), IsEqual.equalTo("Bruce"));

		final Node whiteNode = contactsNode.getChild(new CollectionElement(jessePinkman));
		assertThat(whiteNode.hasChanges(), is(true));

		final Node whiteMiddleNameNode = whiteNode.getChild("middleName");
		assertThat(whiteMiddleNameNode.hasChanges(), is(true));
		assertThat(whiteMiddleNameNode.canonicalGet(phoneBook), IsNull.nullValue());
		assertThat((String) whiteMiddleNameNode.canonicalGet(modifiedPhoneBook), IsEqual.equalTo("Hartwell"));
	}
}
