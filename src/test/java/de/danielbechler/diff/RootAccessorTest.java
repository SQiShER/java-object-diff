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

import de.danielbechler.diff.nodepath.RootElementSelector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsSame.sameInstance;

/**
 * @author Daniel Bechler
 */
public class RootAccessorTest
{
	private Object target;

	@BeforeMethod
	public void setUpTargetObject()
	{
		target = new Object();
	}

	@Test
	public void get_should_return_target_object()
	{
		assertThat(RootAccessor.getInstance().get(target), sameInstance(target));
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void set_should_not_be_supported()
	{
		RootAccessor.getInstance().set(target, new Object());
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void unset_should_not_be_supported()
	{
		RootAccessor.getInstance().unset(target);
	}

	@Test
	public void getPathElement_should_return_RootElement()
	{
		assertThat(RootAccessor.getInstance().getElementSelector(), is(instanceOf(RootElementSelector.class)));
	}

	@Test
	public void toString_should_return_human_readable_string()
	{
		assertThat(RootAccessor.getInstance().toString(), is(equalTo("root element")));
	}
}
