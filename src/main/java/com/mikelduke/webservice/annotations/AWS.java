/**
 * 
 */
package com.mikelduke.webservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mikel
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AWS {
	String path();
	String method() default "*";
	boolean startsWith() default false;
	String description() default "A Webservice";
}
