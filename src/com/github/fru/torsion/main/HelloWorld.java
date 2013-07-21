package com.github.fru.torsion.main;

import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;

@Js(global=true)
public abstract class HelloWorld {

    @Js(action="load")
    public void first(){
        alert("Hello World.");
    }

    @JsNative(inline = "alert(@3);")
    protected abstract void alert(Object object);
}