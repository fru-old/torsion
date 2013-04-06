package com.github.fru.torsion.example;

import java.io.IOException;

import com.github.fru.torsion.bytecode.ClassFile;

public class Main_ByteCode {

	public static void main(String... args) throws IOException {
		System.out.println(ClassFile.parse(A.class).toString());
	}

}
