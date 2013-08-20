/*
 * Copyright 2012 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.integration;

import de.danielbechler.diff.*;
import de.danielbechler.diff.visitor.*;

public class TestCase
{

	// class has public access
	public static class TestIntBeanPublic
	{

		private int value;

		public int getValue()
		{
			return this.value;
		}

		public void setValue(final int value)
		{
			this.value = value;
		}

		@Override
		public String toString()
		{
			return "TestIntBeanPublic [value=" + this.value + "]";
		}
	}

	// class has private access
	private static class TestIntBeanPrivate
	{

		private int value;

		public int getValue()
		{
			return this.value;
		}

		public void setValue(final int value)
		{
			this.value = value;
		}

		@Override
		public String toString()
		{
			return "TestIntBeanPrivate [value=" + this.value + "]";
		}
	}

	public static void testIntegerFailsPublic()
	{

		TestIntBeanPublic working = new TestIntBeanPublic();
		working.setValue(0);

		TestIntBeanPublic base = new TestIntBeanPublic();
		base.setValue(1);

		ObjectDiffer differ = ObjectDifferBuilder.buildDefaultObjectDiffer();
		final DiffNode root = differ.compare(working, base);

		root.visit(new PrintingVisitor(working, base));
	}

	public static void testIntegerWorksPrivate()
	{

		TestIntBeanPrivate working = new TestIntBeanPrivate();
		working.setValue(0);

		TestIntBeanPrivate base = new TestIntBeanPrivate();
		base.setValue(1);

		ObjectDiffer differ = ObjectDifferBuilder.buildDefaultObjectDiffer();
		final DiffNode root = differ.compare(working, base);

		root.visit(new PrintingVisitor(working, base));
	}

	public static void testIntegerWorksPublic()
	{

		TestIntBeanPublic working = new TestIntBeanPublic();
		working.setValue(2);

		TestIntBeanPublic base = new TestIntBeanPublic();
		base.setValue(1);

		ObjectDiffer differ = ObjectDifferBuilder.buildDefaultObjectDiffer();
		final DiffNode root = differ.compare(working, base);

		root.visit(new PrintingVisitor(working, base));
	}

	public static void main(final String[] args)
	{

		System.out.println("The following comparison works properly (detects change)");
		TestCase.testIntegerWorksPublic();
		System.out.println("The following comparison does not work properly (detects removal)");
		TestCase.testIntegerFailsPublic();
		System.out
			  .println("The following comparison works properly bewcause the class has private access (detects change)");
		TestCase.testIntegerWorksPrivate();
	}
}
