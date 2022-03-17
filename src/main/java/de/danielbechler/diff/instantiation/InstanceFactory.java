/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.instantiation;

/**
 * Serves as factory for objects. It is mainly used when {@link de.danielbechler.diff.node.DiffNode#canonicalSet(Object,
 * Object)} is called on a node for which the target returns <code>null</code> for its parent. The parent (and
 * subsequently all of its predecessors along the path) will be created via this factory and inserted into the target
 * object.
 */
public interface InstanceFactory
{
	/**
	 * @param type The type for which a new instance should be created
	 * @return A new instance of the given <code>type</code> or <code>null</code> if it doesn't know how to instantiate
	 * the type. In case of the latter, the {@link de.danielbechler.diff.ObjectDiffer} will automatically fallback to
	 * instantiation via public non-arg constructor. If that also fails, an {@link TypeInstantiationException}
	 * will be thrown.
	 * <p>
	 * <b>Note from the author:</b> it wasn't an easy decision, but in the end I favored an exception over
	 * logging a warning, because this way it is much harder to accidentally end up with incomplete merges without
	 * noticing. If this turns out to be a problem for you, please let me know in the issue tracker. We could probably
	 * add a flag to switch to the logging-only mode. But as long as nobody complains, I'll just leave it as it is right
	 * now.
	 */
	Object newInstanceOfType(Class<?> type);
}
