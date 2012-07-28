package de.danielbechler.diff;

public class InstanceOutline {

	private Object comparisonObject;
	private Object working;
	private Object base;

	public InstanceOutline() {
		super();
	}

	public InstanceOutline(Object comparisonObject, Object working, Object base) {
		super();
		this.comparisonObject = comparisonObject;
		this.working = working;
		this.base = base;
	}


	public static InstanceOutline from(Instances instances) {
		if(instances.getWorking() == null)
			return null;
		if(instances.getBase() == null)
			return null;
		Object comparisonObject = instances.getComparisonObject();
		if(comparisonObject == null)
			return null;
		return new InstanceOutline(comparisonObject, instances.getWorking(), instances.getBase());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + ((comparisonObject == null) ? 0 : comparisonObject.hashCode());
		result = prime * result + ((working == null) ? 0 : working.hashCode());
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
		InstanceOutline other = (InstanceOutline) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (comparisonObject == null) {
			if (other.comparisonObject != null)
				return false;
		} else if (!comparisonObject.equals(other.comparisonObject))
			return false;
		if (working == null) {
			if (other.working != null)
				return false;
		} else if (!working.equals(other.working))
			return false;
		return true;
	}
	
}
