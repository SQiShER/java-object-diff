package de.danielbechler.diff.annotation;

import java.lang.annotation.*;

/** @author Daniel Bechler */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@ObjectDiffAnnotation
public @interface ObjectDiffProperty
{
	public boolean ignore() default false;

	public boolean equalsOnly() default false;

	public String[] categories() default {};
}
