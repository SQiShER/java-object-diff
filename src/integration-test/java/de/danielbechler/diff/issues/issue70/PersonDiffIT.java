package de.danielbechler.diff.issues.issue70;

import de.danielbechler.diff.visit.PrintingVisitor;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.helper.NodeAssertions;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.path.NodePath;
import org.testng.annotations.Test;

import java.util.Arrays;

public class PersonDiffIT
{
	@Test
	public void testIncludeCollectionAttribute()
	{
		final Person a = new Person("Gulen Chongtham", Arrays.asList("Hola Espanyol", "Vicky Boss"));
		final Person b = new Person("Gulen Chongthamm", Arrays.asList("Hola Espanyol", "Vicky Boss", "Roger Harper"));

		final ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
		builder.configure().inclusion().include().node(NodePath.with("aliases"));
		final ObjectDiffer differ = builder.build();

		final DiffNode root = differ.compare(b, a);
		root.visit(new PrintingVisitor(b, a));

		NodeAssertions.assertThat(root).root().hasChanges();
	}
}
