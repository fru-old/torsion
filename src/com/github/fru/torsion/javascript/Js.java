package com.github.fru.torsion.javascript;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Js {

	boolean global() default false;
	String name() default "";
	String action() default "";
	
}
