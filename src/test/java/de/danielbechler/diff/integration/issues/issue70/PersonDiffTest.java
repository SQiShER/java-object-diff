package de.danielbechler.diff.integration.issues.issue70;

import de.danielbechler.diff.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import org.testng.annotations.*;

import java.util.*;

public class PersonDiffTest
{
	@Test
	public void testIncludeCollectionAttribute()
	{
		final Person a = new Person("Gulen Chongtham", Arrays.asList("Hola Espanyol", "Vicky Boss"));
		final Person b = new Person("Gulen Chongthamm", Arrays.asList("Hola Espanyol", "Vicky Boss", "Roger Harper"));

		final ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
		builder.configure().inclusion().toInclude().nodes(NodePath.buildWith("aliases"));
		final ObjectDiffer differ = builder.build();

		final DiffNode root = differ.compare(b, a);
		root.visit(new PrintingVisitor(b, a));

		NodeAssertions.assertThat(root).root().hasChanges();
	}
}
