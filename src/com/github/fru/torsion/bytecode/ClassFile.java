package com.github.fru.torsion.bytecode;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.MethodBody;

public class ClassFile {
	
	public static HashMap<AccessibleObject,MethodBody> parse(Class<?> c) throws IOException {
		return new ClassFile().parse(new ByteInputStream(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(c.getName().replace('.', '/') + ".class")),c);
	}

	final HashMap<AccessibleObject,MethodBody> result = new HashMap<AccessibleObject, MethodBody>(); 
	final HashMap<Integer, ClassFileConstant> constants = new HashMap<Integer, ClassFileConstant>();

	@SuppressWarnings("unused")
	private HashMap<AccessibleObject,MethodBody> parse(ByteInputStream reader, Class<?> clazz) throws IOException {
		// PARSE: Magic number
		int[] array = new int[] { 0xCA, 0xFE, 0xBA, 0xBE };

		for (int a : array) {
			int next = reader.findNext();
			if (a != next)
				throw new IOException(
						"Found ["+next+"] but expected ["+a+"] as in ["+array+"] at "+reader.getPosition() );
		}

		// PARSE: Version
		int minorVersion = reader.findShort();
		int majorVersion = reader.findShort();

		// PARSE: Constant table
		int constantPoolCount = reader.findShort();

		for (int c = 1; c < constantPoolCount; c++) {
			ClassFileConstant constant = ClassFileConstant.parse(reader);
			constants.put(c, constant);
			if (constant.getType() == ClassFileConstant.ClassFileConstantType.Long
					|| constant.getType() == ClassFileConstant.ClassFileConstantType.Double) {
				c++; // ick
			}
		}

		// PARSE: Class Information
		int accessFlags = reader.findShort();
		int thisClass = reader.findShort();
		int superClass = reader.findShort();

		int interfaceCount = reader.findShort();
		int[] interfaces = new int[interfaceCount];
		for (int i = 0; i < interfaces.length; i++) {
			interfaces[i] = reader.findShort();
		}

		// PARSE: Fields
		int fieldCount = reader.findShort();
		int[] fieldsFlag = new int[fieldCount];
		int[] fieldsName = new int[fieldCount];
		int[] fieldsType = new int[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
			fieldsFlag[i] = reader.findShort();
			fieldsName[i] = reader.findShort();
			fieldsType[i] = reader.findShort();
			findAttributes(reader,null,clazz);
		}

		// PARSE: Methods
		int methodCount = reader.findShort();
		int[] methodsFlag = new int[methodCount];
		int[] methodsName = new int[methodCount];
		int[] methodsType = new int[methodCount];
		for (int i = 0; i < methodCount; i++) {
			methodsFlag[i] = reader.findShort();
			methodsName[i] = reader.findShort();
			methodsType[i] = reader.findShort();
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
		int attributeCount = reader.findShort();
		for (int i = 0; i < attributeCount; i++) {
			int name = reader.findShort();
			int size = reader.findInt(); // attribute length
			parseAttribute(new ByteInputStream(reader, size), constants.get(name).value, current, clazz);
		}
	}

	@SuppressWarnings("unused")
	private void parseAttribute(ByteInputStream reader, String name, AccessibleObject current, Class<?> clazz) throws IOException {
		if("code".equalsIgnoreCase(name)){
			int maxStack = reader.findShort();
		    int maxLocal = reader.findShort();
		    
		    MethodBody body = new MethodBody();
		    body.parseBody(reader, constants, clazz);
		    
		    result.put(current, body);
		    
		    int tablelength = reader.findShort();
		    for(int i = 0; i < tablelength; i++){
		      int from = reader.findShort();
		      int to = reader.findShort();
		      int jump = reader.findShort();
		      int exception = reader.findShort();
		      if(exception == 0)System.out.println(String.format( "Handle all exception from [%s] to [%s] then jump to [%S]",from,to,jump ));
		      else System.out.println(String.format( "Handle exception [%s] from [%s] to [%s] then jump to [%S]", exception,from,to,jump ));
		    }

		    findAttributes( reader, current, clazz);
		}else{
			try {
				while (true)
					reader.findNext();
			} catch (EOFException exception) {
				// Expected
			}
		}
	}
}
