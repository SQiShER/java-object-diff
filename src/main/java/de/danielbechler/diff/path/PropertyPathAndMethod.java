package de.danielbechler.diff.path;

public class PropertyPathAndMethod {
	private PropertyPath propertyPath;
	private String method;
	
	public PropertyPathAndMethod(){}
	public PropertyPathAndMethod(PropertyPath propertyPath, String method){
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
