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

package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import org.hamcrest.core.*;
import org.mockito.*;
import org.testng.annotations.*;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

/** @author Daniel Bechler */
public class DelegatingObjectDifferTest
{
	@Mock
	private Differ beanDiffer;
	@Mock
	private Differ mapDiffer;
	@Mock
	private Differ collectionDiffer;
	@Mock
	private Accessor accessor;
	@Mock
	private Node node;

	private DelegatingObjectDifferImpl differ;

	@BeforeMethod
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		differ = new DelegatingObjectDifferImpl(beanDiffer, mapDiffer, collectionDiffer);
	}

	@Test
	public void testConstructionWithDefaultConstructor() throws Exception
	{
		differ = new DelegatingObjectDifferImpl();
		assertThat(differ.getBeanDiffer(), IsNull.notNullValue());
		assertThat(differ.getMapDiffer(), IsNull.notNullValue());
		assertThat(differ.getCollectionDiffer(), IsNull.notNullValue());
	}

	@Test
	public void testCompareWithCollection() throws Exception
	{
		differ.delegate(Node.ROOT, Instances.of(new RootAccessor(), new LinkedList<String>(), new LinkedList<String>()));
		Mockito.verify(collectionDiffer, Mockito.times(1)).compare(any(Node.class), any(Instances.class));
		Mockito.verify(mapDiffer, Mockito.never()).compare(any(Node.class), any(Instances.class));
		Mockito.verify(beanDiffer, Mockito.never()).compare(any(Node.class), any(Instances.class));
	}

	@Test
	public void testCompareWithMap() throws Exception
	{
		differ.delegate(Node.ROOT, Instances.of(new TreeMap<String, String>(), new TreeMap<String, String>()));
		Mockito.verify(collectionDiffer, Mockito.never()).compare(any(Node.class), any(Instances.class));
		Mockito.verify(mapDiffer, Mockito.times(1)).compare(any(Node.class), any(Instances.class));
		Mockito.verify(beanDiffer, Mockito.never()).compare(any(Node.class), any(Instances.class));
	}

	@Test
	public void testCompareWithSimpleType() throws Exception
	{
		differ.delegate(Node.ROOT, Instances.of("", ""));
		Mockito.verify(collectionDiffer, Mockito.never()).compare(any(Node.class), any(Instances.class));
		Mockito.verify(mapDiffer, Mockito.never()).compare(any(Node.class), any(Instances.class));
		Mockito.verify(beanDiffer, Mockito.times(1)).compare(any(Node.class), any(Instances.class));
	}

	@Test
	public void testCompareWithBean() throws Exception
	{
		differ.compare(new ObjectWithString(), new ObjectWithString());
		Mockito.verify(collectionDiffer, Mockito.never()).compare(any(Node.class), any(Instances.class));
		Mockito.verify(mapDiffer, Mockito.never()).compare(any(Node.class), any(Instances.class));
		Mockito.verify(beanDiffer, Mockito.times(1)).compare(any(Node.class), any(Instances.class));
	}

	@Test
	public void testGetConfiguration() throws Exception
	{
		assertThat(differ.getConfiguration(), IsNull.notNullValue());
	}

	@Test
	public void testSetConfiguration() throws Exception
	{
		final Configuration configuration = new Configuration();
		differ.setConfiguration(configuration);
		assertThat(differ.getConfiguration(), IsSame.sameInstance(configuration));
	}

	@Test
	public void testCompareWithIgnoredMapProperty()
	{
		final ObjectWithIgnoredMap working = new ObjectWithIgnoredMap();
		working.getMap().put("foo", "bar");
		final ObjectWithIgnoredMap base = new ObjectWithIgnoredMap();
		final ObjectDiffer objectDiffer = new DelegatingObjectDifferImpl();
		final Node node = objectDiffer.compare(working, base);
		assertThat(node.hasChanges(), Is.is(false));
		assertThat(node.hasChildren(), Is.is(false));
	}

	@Test
	public void testCompareWithIgnoredCollectionProperty()
	{
		final ObjectWithCollection working = new ObjectWithCollection();
		working.getCollection().add("foo");
		final ObjectWithCollection base = new ObjectWithCollection();
		final ObjectDiffer objectDiffer = new DelegatingObjectDifferImpl();
		objectDiffer.getConfiguration().withoutProperty(PropertyPath.buildWith("collection"));
		final Node node = objectDiffer.compare(working, base);
		node.visit(new PrintingVisitor(working, base));
		assertThat(node.hasChanges(), Is.is(false));
		assertThat(node.hasChildren(), Is.is(false));
	}
}
