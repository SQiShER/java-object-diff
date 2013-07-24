package de.danielbechler.diff.path;

public class MethodEqualClassAndMethod {
	private Class<?> clazz;
	private String method;
	public MethodEqualClassAndMethod(Class<?> clazz, String method){
		this.clazz = clazz;
		this.method = method;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public String getMethod() {
		return method;
	}
}
