/*
 * Copyright 2013 Daniel Bechler
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

import de.danielbechler.diff.mock.*;
import org.testng.annotations.*;

import java.util.*;

/** @author Daniel Bechler */
public class DifferAcceptTypeDataProvider
{
	@DataProvider
	public static Object[][] collectionTypes()
	{
		return new Object[][] {
				new Object[] {Collection.class},
				new Object[] {List.class},
				new Object[] {Queue.class},
				new Object[] {Set.class},
				new Object[] {ArrayList.class},
				new Object[] {LinkedList.class},
		};
	}

	@DataProvider
	public static Object[][] mapTypes()
	{
		return new Object[][] {
				new Object[] {Map.class},
				new Object[] {HashMap.class},
				new Object[] {TreeMap.class},
		};
	}

	@DataProvider
	public static Object[][] beanTypes()
	{
		return new Object[][] {
				new Object[] {Object.class},
				new Object[] {ObjectWithString.class},
				new Object[] {Date.class},
		};
	}

	@DataProvider
	public Object[][] primitiveTypes()
	{
		return new Object[][] {
				new Object[] {int.class},
				new Object[] {short.class},
				new Object[] {char.class},
				new Object[] {long.class},
				new Object[] {boolean.class},
				new Object[] {byte.class},
				new Object[] {float.class},
				new Object[] {double.class},
		};
	}
}
