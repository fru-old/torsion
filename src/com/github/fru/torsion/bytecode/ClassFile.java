package com.github.fru.torsion.bytecode;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.normalization.Block;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;

public class ClassFile {
	
	public static HashMap<AccessibleObject,Body> parse(Class<?> c) throws IOException {
		return new ClassFile().parse(new ByteInputStream(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(c.getName().replace('.', '/') + ".class")),c);
	}

	final HashMap<AccessibleObject,Body> result = new HashMap<AccessibleObject, Body>(); 
	final HashMap<Integer, ClassFileConstant> constants = new HashMap<Integer, ClassFileConstant>();

	@SuppressWarnings("unused")
	private HashMap<AccessibleObject,Body> parse(ByteInputStream reader, Class<?> clazz) throws IOException {
		// PARSE: Magic number
		int[] array = new int[] { 0xCA, 0xFE, 0xBA, 0xBE };

		for (int a : array) {
			int next = reader.nextByte();
			if (a != next)
				throw new IOException(
						"Found ["+next+"] but expected ["+a+"] as in ["+array+"] at "+reader.getPosition() );
		}

		// PARSE: Version
		int minorVersion = reader.nextShort();
		int majorVersion = reader.nextShort();

		// PARSE: Constant table
		int constantPoolCount = reader.nextShort();

		for (int c = 1; c < constantPoolCount; c++) {
			ClassFileConstant constant = ClassFileConstant.parse(reader);
			constants.put(c, constant);
			if (constant.getType() == ClassFileConstant.ClassFileConstantType.Long
					|| constant.getType() == ClassFileConstant.ClassFileConstantType.Double) {
				c++; // ick
			}
		}

		// PARSE: Class Information
		int accessFlags = reader.nextShort();
		int thisClass = reader.nextShort();
		int superClass = reader.nextShort();

		int interfaceCount = reader.nextShort();
		int[] interfaces = new int[interfaceCount];
		for (int i = 0; i < interfaces.length; i++) {
			interfaces[i] = reader.nextShort();
		}

		// PARSE: Fields
		int fieldCount = reader.nextShort();
		int[] fieldsFlag = new int[fieldCount];
		int[] fieldsName = new int[fieldCount];
		int[] fieldsType = new int[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
			fieldsFlag[i] = reader.nextShort();
			fieldsName[i] = reader.nextShort();
			fieldsType[i] = reader.nextShort();
			findAttributes(reader,null,clazz);
		}

		// PARSE: Methods
		int methodCount = reader.nextShort();
		int[] methodsFlag = new int[methodCount];
		int[] methodsName = new int[methodCount];
		int[] methodsType = new int[methodCount];
		for (int i = 0; i < methodCount; i++) {
			methodsFlag[i] = reader.nextShort();
			methodsName[i] = reader.nextShort();
			methodsType[i] = reader.nextShort();
			AccessibleObject method = Identifier.parseMethodConstant(methodsName[i], methodsType[i], clazz, constants);
			findAttributes(reader,method,clazz);
		}

		// PARSE: Info
		findAttributes(reader, null, clazz);

		if (reader.getInput().read() != -1) { // The end should be reached
			throw new IOException("Expected end of file at "+reader.getPosition());
		}
		return result;
	}

	private void findAttributes(ByteInputStream reader, AccessibleObject current, Class<?> clazz) throws IOException {
		int attributeCount = reader.nextShort();
		for (int i = 0; i < attributeCount; i++) {
			int name = reader.nextShort();
			int size = reader.nextInt(); // attribute length
			parseAttribute(new ByteInputStream(reader, size), constants.get(name).getConstant(), current, clazz);
		}
	}

	@SuppressWarnings("unused")
	private void parseAttribute(ByteInputStream reader, String name, AccessibleObject current, Class<?> clazz) throws IOException {
		if("code".equalsIgnoreCase(name)){
			int maxStack = reader.nextShort();
		    int maxLocal = reader.nextShort();
		    
		    Body body = new Body();
		    body.parseBody(reader, constants, current, clazz);
		    
		    result.put(current, body);
		    
		    int tablelength = reader.nextShort();
		    for(int i = 0; i < tablelength; i++){
		      int from = reader.nextShort();
		      int to = reader.nextShort();
		      int jump = reader.nextShort();
		      int exception = reader.nextShort();
		      if(exception == 0)System.out.println(String.format( "Handle all exception from [%s] to [%s] then jump to [%S]",from,to,jump ));
		      else System.out.println(String.format( "Handle exception [%s] from [%s] to [%s] then jump to [%S]", exception,from,to,jump ));
		    }
		    
		    Block.applyBlocks(body.body);

		    findAttributes( reader, current, clazz);
		}else{
			try {
				while (true)
					reader.nextByte();
			} catch (EOFException exception) {
				// Expected
			}
		}
	}
}
