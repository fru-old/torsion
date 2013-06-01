package com.github.fru.torsion.main;

import java.lang.reflect.AccessibleObject;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.normalization.Body;

public class Main_ByteCode {

	public static void main(String... args) throws Exception {
		HashMap<AccessibleObject, Body> result = ClassFile.parse(C.class);
		for(AccessibleObject o : result.keySet()){
			System.out.println("+++ "+o+" +++");
			System.out.println(result.get(o).toString());
			System.out.println("");
		}		
	}

}
