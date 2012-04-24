package de.danielbechler.diff.annotation;

import java.lang.annotation.*;

/** @author Daniel Bechler */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ObjectDiffAnnotation
public @interface ObjectDiffEqualsOnlyType
{
}
