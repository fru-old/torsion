package com.github.fru.torsion.main;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.normalization.MethodBody;

public class Main_ByteCode {

	public static void main(String... args) throws IOException {
		HashMap<AccessibleObject, MethodBody> result = ClassFile.parse(B.class);
		for(AccessibleObject o : result.keySet()){
			System.out.println("+++ "+o+" +++");
			System.out.println(result.get(o).toString());
			System.out.println("");
		}
	}

}
