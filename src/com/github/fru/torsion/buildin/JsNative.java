package com.github.fru.torsion.buildin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JsNative {
	String[] inline() default "";
}
