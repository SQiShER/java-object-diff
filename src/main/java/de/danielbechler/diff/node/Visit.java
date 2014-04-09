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

/**
 * @author Daniel Bechler
 */
public final class Visit
{
	private enum State
	{
		CONTINUE,
		CONTINUE_BUT_DO_NOT_GO_DEEPER,
		STOPPED
	}

	private State state = State.CONTINUE;

	public void stop()
	{
		state = State.STOPPED;
	}

	public void dontGoDeeper()
	{
		state = State.CONTINUE_BUT_DO_NOT_GO_DEEPER;
	}

	public boolean isStopped()
	{
		return state == State.STOPPED;
	}

	public boolean isAllowedToGoDeeper()
	{
		return state == State.CONTINUE;
	}
}
