package de.danielbechler.diff.integration.issues.issue70;

import de.danielbechler.diff.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@RunWith(JUnit4.class)
public class PersonDiffTest
{
	@Test
	public void testIncludeCollectionAttribute()
	{
		final Person a = new Person("Gulen Chongtham", Arrays.asList("Hola Espanyol", "Vicky Boss"));
		final Person b = new Person("Gulen Chongthamm", Arrays.asList("Hola Espanyol", "Vicky Boss", "Roger Harper"));

		final ObjectDiffer differ = ObjectDifferFactory.getInstance(new Configuration().withPropertyPath(PropertyPath.buildWith("aliases")));

		final Node root = differ.compare(b, a);
		root.visit(new PrintingVisitor(b, a));

		NodeAssertions.assertThat(root).root().hasChanges();
	}
}
