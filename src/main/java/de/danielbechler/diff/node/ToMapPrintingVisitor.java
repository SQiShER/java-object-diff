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

package de.danielbechler.diff.node;

import de.danielbechler.diff.path.NodePath;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This visitor generates the same output as the {@link PrintingVisitor}, but instead of printing it to
 * <code>System.out</code>, it will store the messages in a Map, identified by the property path of the
 * visited node. The {@link Map} can later be accessed via {@link #getMessages()} to provide a way to
 * post-process the collected information.
 *
 * @author Daniel Bechler (SQiShER)
 * @author Mayank Kumar (mayankk)
 */
public class ToMapPrintingVisitor extends PrintingVisitor
{
	private final Map<NodePath, String> messages = new LinkedHashMap<NodePath, String>();

	public ToMapPrintingVisitor(final Object working, final Object base)
	{
		super(working, base);
	}

	@Override
	protected void print(final String text)
	{
	}

	@Override
	protected String differenceToString(final DiffNode node, final Object base, final Object modified)
	{
		final String text = super.differenceToString(node, base, modified);
		messages.put(node.getPath(), text);
		return text;
	}

	public void clear()
	{
		messages.clear();
	}

	public Map<NodePath, String> getMessages()
	{
		return messages;
	}

	public String getMessage(final NodePath path)
	{
		return messages.get(path);
	}

	public boolean hasMessages()
	{
		return !messages.isEmpty();
	}

	public String getMessagesAsString()
	{
		final StringBuilder sb = new StringBuilder();
		for (final String message : messages.values())
		{
			sb.append(message).append('\n');
		}
		return sb.toString();
	}

	@Override
	public String toString()
	{
		return getMessagesAsString();
	}
}
