package de.danielbechler.diff;

import org.mockito.invocation.*;
import org.mockito.stubbing.*;

/** @author Daniel Bechler */
public class MockitoAnswers
{
	private MockitoAnswers()
	{
	}

	public static <T> Answer<Class<T>> withType(final Class<T> type)
	{
		return new TypeAnswer<T>(type);
	}

	private static class TypeAnswer<T> implements Answer<Class<T>>
	{
		private final Class<T> type;

		public TypeAnswer(final Class<T> type)
		{
			this.type = type;
		}

		public Class<T> answer(final InvocationOnMock invocation) throws Throwable
		{
			return type;
		}
	}
}
