package com.github.fru.torsion.main;

import java.io.IOException;

import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.map.BytecodeParser;

public class Main_ByteCode {

	public static void main(String... args) throws IOException {
		System.out.println(ClassFile.parse(B.class).toString());
		
		//System.out.println("Not supported operations: \n");
		for(int i = 0; i < 0xCA; i++){
			//if(!BytecodeParser.isBytecodeSupported(i))System.out.println(i);
		}
	}

}
