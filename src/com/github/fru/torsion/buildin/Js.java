package com.github.fru.torsion.buildin;

public @interface Js {

	boolean global() default false;
	String name() default "";
	
}
