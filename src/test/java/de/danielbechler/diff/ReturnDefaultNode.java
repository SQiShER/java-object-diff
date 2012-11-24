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

import de.danielbechler.diff.node.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;

/** @author Daniel Bechler */
class ReturnDefaultNode implements Answer<Node>
{
	private final Node.State state;

	ReturnDefaultNode(final Node.State state)
	{
		this.state = state;
	}

	@Override
	public Node answer(final InvocationOnMock invocation) throws Throwable
	{
		final Node parentNode = (Node) invocation.getArguments()[0];
		final Instances instances = (Instances) invocation.getArguments()[1];
		final Node node = new DefaultNode(parentNode, instances.getSourceAccessor(), instances.getType());
		node.setState(state);
		return node;
	}
}
