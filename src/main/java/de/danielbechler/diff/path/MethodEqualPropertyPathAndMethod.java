package de.danielbechler.diff.path;

public class MethodEqualPropertyPathAndMethod {
	private PropertyPath propertyPath;
	private String method;
	
	public MethodEqualPropertyPathAndMethod(){}
	public MethodEqualPropertyPathAndMethod(PropertyPath propertyPath, String method){
		this.propertyPath = propertyPath;
		this.method = method;
	}
	
	public PropertyPath getPropertyPath() {
		return propertyPath;
	}
	
	public String getMethod() {
		return method;
	}
}
