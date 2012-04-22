package de.danielbechler.diff.annotation;

import java.lang.annotation.*;

/** @author Daniel Bechler */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectDiffAnnotation
{
}
