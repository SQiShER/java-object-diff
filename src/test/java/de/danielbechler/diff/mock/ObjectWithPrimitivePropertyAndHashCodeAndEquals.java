package de.danielbechler.diff.mock;

public class ObjectWithPrimitivePropertyAndHashCodeAndEquals {
	
	private int primitive;

	public int getPrimitive() {
		return primitive;
	}

	public void setPrimitive(int primitive) {
		this.primitive = primitive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + primitive;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectWithPrimitivePropertyAndHashCodeAndEquals other = (ObjectWithPrimitivePropertyAndHashCodeAndEquals) obj;
		if (primitive != other.primitive)
			return false;
		return true;
	}
	
	
}
