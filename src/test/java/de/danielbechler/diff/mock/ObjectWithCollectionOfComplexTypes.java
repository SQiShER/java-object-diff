package de.danielbechler.diff.mock;

import java.util.ArrayList;
import java.util.List;

public class ObjectWithCollectionOfComplexTypes {
	
	private List<ObjectWithPrimitivePropertyAndHashCodeAndEquals> list = new ArrayList<ObjectWithPrimitivePropertyAndHashCodeAndEquals>();

	public List<ObjectWithPrimitivePropertyAndHashCodeAndEquals> getList() {
		return list;
	}
}
