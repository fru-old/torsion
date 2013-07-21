Torsion
=======

Generate clientside Javascript from Java. This is a proof of concept for a crosscompiler using java bytecode. 
Torsion should not be used for production for it is in pre-alpha state. 

For anybody still interested a first result demonstrating a first hello world alert box can be seen below:
```
@Js(global=true)
public abstract class HelloWorld {

  @Js(action="load")
	public void first(){
		alert("Hello World.");
	}
	
	@JsNative(inline = "alert(@3);")
	protected abstract void alert(Object object);
}

```


