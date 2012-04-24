/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.danielbechler.diff.integration;

import de.danielbechler.diff.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.visitor.*;

/** @author Daniel Bechler */
public class GettingStarted
{
	private GettingStarted()
	{
	}

	public static void main(final String[] args)
	{
		helloWorldExample();
		phoneBookExample();
	}

	private static void helloWorldExample()
	{
		final ObjectDiffer objectDiffer = ObjectDifferFactory.getInstance();

		final String working = "Hello";
		final String base = "World";
		final Node root = objectDiffer.compare(working, base);

		root.visit(new PrintingVisitor(working, base));
	}

	private static void phoneBookExample()
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
	final Node root = objectDiffer.compare(modifiedPhoneBook, phoneBook);
	final Node.Visitor visitor = new PrintingVisitor(modifiedPhoneBook, phoneBook);
	root.visit(visitor);
	}
}
