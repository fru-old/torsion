package com.github.fru.torsion.main;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.github.fru.torsion.example.Generator;

public class Main {
	public static void main(String... args) throws IOException{
		boolean local = false;
		Class<?> clazz = HelloWorld.class; 
		
		if(local){
			Generator.printSimpleJavascript(clazz);
		}else{
			File html = Generator.generateSimpleFiles(clazz);
			Desktop.getDesktop().browse(html.toURI());
		}
	}
}
