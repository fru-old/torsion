Torsion
=======

Generate clientside Javascript from Java. This is a proof of concept for a crosscompiler using java bytecode. 
Torsion should not be used for production, for it is in pre-alpha state. 

For anybody still interested a first result, demonstrating a hello world alert box, can be seen below:
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

To generate result files and run them one needs to register the new HelloWorld Java class:
```
public static void main(String... args) throws IOException{
	File html = Generator.generateSimpleFiles(HelloWorld.class);
	Desktop.getDesktop().browse(html.toURI());
}
```

A more complex example reveals, that basic types can be used as well as functional pointers:
```
@Js(global=true)
public abstract class Complex {

	@Js(action="load")
	public void first(){
		String i = inc(13)+"";
		
		alert("test"+i);
		new Action.Arg0("func").call();
	}
	
	@Js(name="func")
	protected void func(){
		alert("1233");
	}
	
	@JsNative(inline = "alert(@3);")
	protected abstract void alert(Object object);
	
	public int inc(int x){
		return x + 1;
	}
}

```
