package de.danielbechler.diff.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@ObjectDiffAnnotation
public @interface ObjectDiffEqualsOnlyValueProvidedType {
	public String method();
}
