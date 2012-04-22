package de.danielbechler.diff.annotation;

import java.lang.annotation.*;

/** @author Daniel Bechler */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@ObjectDiffAnnotation
public @interface ObjectDiffProperty
{
	public boolean ignore() default true;

	public boolean equalsOnly() default true;

	public String[] categories() default {};
}
