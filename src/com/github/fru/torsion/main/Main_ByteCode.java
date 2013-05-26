package com.github.fru.torsion.main;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.normalization.MethodBody;

public class Main_ByteCode {

	public static void main(String... args) throws IOException {
		HashMap<Method, MethodBody> result = ClassFile.parse(B.class);
	
		//System.out.println("Not supported operations: \n");
		for(int i = 0; i < 0xCA; i++){
			//if(!BytecodeParser.isBytecodeSupported(i))System.out.println(i);
		}
	}

}
