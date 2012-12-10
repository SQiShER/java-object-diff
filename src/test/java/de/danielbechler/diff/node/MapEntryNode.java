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

package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;

import java.util.*;

/**
 * This class is not yet ready. Probably turns out to be useless.
 *
 * @author Daniel Bechler
 */
class MapEntryNode extends DefaultNode
{
	private final MapEntryAccessor mapEntryAccessor;

	@SuppressWarnings("TypeMayBeWeakened")
	public MapEntryNode(final MapNode parentNode, final MapEntryAccessor mapEntryAccessor)
	{
		super(parentNode, mapEntryAccessor, null);
		this.mapEntryAccessor = mapEntryAccessor;
	}

	public Object getValue(final Map<?, ?> targetMap)
	{
		return mapEntryAccessor.get(targetMap);
	}

	public Collection<Node> getValueChildren()
	{
		return Collections.emptyList();
	}

	public Object getKey(final Map<?, ?> targetMap)
	{
		return mapEntryAccessor.getKey(targetMap);
	}

	public Collection<Node> getKeyChildren()
	{
		return Collections.emptyList();
	}
}
