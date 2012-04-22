package de.danielbechler.diff.visitor;

/** @author Daniel Bechler */
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
