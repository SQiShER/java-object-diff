package de.danielbechler.diff.introspect;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;
import org.hamcrest.core.*;
import org.junit.Assert;
import org.junit.*;

import java.beans.*;

/** @author Daniel Bechler */
public class StandardIntrospectorTest
{
	private StandardIntrospector introspector;

	@Before
	public void setUp() throws Exception
	{
		introspector = new StandardIntrospector();
	}

	@Test
	public void testIntrospectWithEqualsOnlyPropertyType() throws Exception
	{
		final Iterable<Accessor> accessors = introspector.introspect(ObjectWithEqualsOnlyPropertyType.class);
		Assert.assertThat(accessors.iterator().hasNext(), Is.is(true));
		Assert.assertThat(accessors.iterator().next().isEqualsOnly(), Is.is(true));
	}

	@Test
	public void testIntrospectWithPropertyAnnotations()
	{
		final Iterable<Accessor> accessors = introspector.introspect(ObjectWithPropertyAnnotations.class);
		for (final Accessor accessor : accessors)
		{
			if (accessor.getPathElement().equals(new NamedPropertyElement("ignored")))
			{
				Assert.assertThat(accessor.isIgnored(), Is.is(true));
			}
			else if (accessor.getPathElement().equals(new NamedPropertyElement("equalsOnly")))
			{
				Assert.assertThat(accessor.isEqualsOnly(), Is.is(true));
			}
			else if (accessor.getPathElement().equals(new NamedPropertyElement("categorized")))
			{
				Assert.assertThat(accessor.getCategories().size(), Is.is(1));
				Assert.assertThat(accessor.getCategories(), IsEqual.equalTo(Collections.setOf("foo")));
			}
			else if (accessor.getPathElement().equals(new NamedPropertyElement("item")))
			{
				Assert.assertThat(accessor.isEqualsOnly(), Is.is(false));
				Assert.assertThat(accessor.isIgnored(), Is.is(false));
				Assert.assertThat(accessor.getCategories().isEmpty(), Is.is(true));
			}
			else if (accessor.getPathElement().equals(new NamedPropertyElement("key")))
			{
				// no op
			}
			else if (accessor.getPathElement().equals(new NamedPropertyElement("value")))
			{
				// no op
			}
			else
			{
				Assert.fail("Unexpected accessor: " + accessor.getPathElement());
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIntrospectWithNullType() throws Exception
	{
		introspector.introspect(null);
	}

	@Test(expected = RuntimeException.class)
	public void testIntrospectWithSimulatedIntrospectionException() throws Exception
	{
		introspector = new StandardIntrospector()
		{
			@Override
			protected BeanInfo getBeanInfo(final Class<?> type) throws IntrospectionException
			{
				throw new IntrospectionException(type.getCanonicalName());
			}
		};
		introspector.introspect(ObjectWithString.class);
	}
}
