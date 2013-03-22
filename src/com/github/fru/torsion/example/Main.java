package com.github.fru.torsion.example;

import java.io.IOException;

import com.github.fru.torsion.parser.JavaClassFile;

public class Main {

	public static void main(String... args) throws IOException{
		System.out.println(JavaClassFile.parse(A.class).toString());
	}
}
