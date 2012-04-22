package de.danielbechler.diff.annotation;

import java.lang.annotation.*;

/** @author Daniel Bechler */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ObjectDiffAnnotation
@Deprecated
public @interface DiffIgnore
{
}
