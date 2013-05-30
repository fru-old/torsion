package com.github.fru.torsion.main;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.normalization.Block;
import com.github.fru.torsion.bytecode.normalization.Body;

public class Main_ByteCode {

	public static void main(String... args) throws IOException, ClassNotFoundException, SecurityException, NoSuchFieldException {
		HashMap<AccessibleObject, Body> result = ClassFile.parse(A.class);
		for(AccessibleObject o : result.keySet()){
			Block.applyBlocks(result.get(o).body);
			System.out.println("+++ "+o+" +++");
			System.out.println(result.get(o).toString());
			System.out.println("");
		}
	}

}
