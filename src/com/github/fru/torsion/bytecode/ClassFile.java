package com.github.fru.torsion.bytecode;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.github.fru.torsion.bytecode.map.BytecodeParser;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.CodeList;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Variable;

public class ClassFile {

	public static ClassFile parse(String name) throws IOException {
		return new ClassFile(new ByteInputStream(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(name.replace('.', '/') + ".class")));
	}

	public static ClassFile parse(java.lang.Class<?> c) throws IOException {
		return ClassFile.parse(c.getName());
	}

	private ClassFile(ByteInputStream reader) throws IOException {
		// PARSE: Magic number
		int[] array = new int[] { 0xCA, 0xFE, 0xBA, 0xBE };

		for (int a : array) {
			int next = reader.findNext();
			if (a != next)
				throw new IOException(
						"Found ["+next+"] but expected ["+a+"] as in ["+array+"] at "+reader.getPosition() );
		}

		// PARSE: Version
		minorVersion = reader.findShort();
		majorVersion = reader.findShort();

		// PARSE: Constant table
		int constantPoolCount = reader.findShort();
		this.constants = new HashMap<Integer, ClassFileConstant>();

		for (int c = 1; c < constantPoolCount; c++) {
			ClassFileConstant constant = ClassFileConstant.parse(reader);
			constants.put(c, constant);
			if (constant.getType() == ClassFileConstant.ClassFileConstantType.Long
					|| constant.getType() == ClassFileConstant.ClassFileConstantType.Double) {
				c++; // ick
			}
		}

		// PARSE: Class Information
		accessFlags = reader.findShort();
		thisClass = reader.findShort();
		superClass = reader.findShort();

		int interfaceCount = reader.findShort();
		interfaces = new int[interfaceCount];
		for (int i = 0; i < interfaces.length; i++) {
			interfaces[i] = reader.findShort();
		}

		// PARSE: Fields
		int fieldCount = reader.findShort();
		fieldsFlag = new int[fieldCount];
		fieldsName = new int[fieldCount];
		fieldsType = new int[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
			fieldsFlag[i] = reader.findShort();
			fieldsName[i] = reader.findShort();
			fieldsType[i] = reader.findShort();
			findAttributes(reader);
		}

		// PARSE: Methods
		int methodCount = reader.findShort();
		methodsFlag = new int[methodCount];
		methodsName = new int[methodCount];
		methodsType = new int[methodCount];
		methodsInstructions = new ArrayList<CodeList<Instruction>>();
		for (int i = 0; i < methodCount; i++) {
			methodsFlag[i] = reader.findShort();
			methodsName[i] = reader.findShort();
			methodsType[i] = reader.findShort();
			findAttributes(reader);
		}

		// PARSE: Info
		findAttributes(reader);

		if (reader.getInput().read() != -1) { // The end should be reached
			throw new IOException("Expected end of file at "+reader.getPosition());
		}
	}

	Map<Integer, ClassFileConstant> constants;

	int minorVersion;
	int majorVersion;

	int accessFlags;
	int thisClass;
	int superClass;
	int[] interfaces;

	int[] fieldsFlag;
	int[] fieldsName;
	int[] fieldsType;

	int[] methodsFlag;
	int[] methodsName;
	int[] methodsType;
	
	ArrayList<CodeList<Instruction>> methodsInstructions;

	public void findAttributes(ByteInputStream reader) throws EOFException {
		int attributeCount = reader.findShort();
		for (int i = 0; i < attributeCount; i++) {
			int name = reader.findShort();
			int size = reader.findInt(); // attribute length
			parseAttribute(new ByteInputStream(reader, size),
					constants.get(name).value);
		}
	}

	@SuppressWarnings("unused")
	public void parseAttribute(ByteInputStream reader, String name) throws EOFException {
		if("code".equalsIgnoreCase(name)){
			int maxStack = reader.findShort();
		    int maxLocal = reader.findShort();
		    
		    CodeList<Instruction> code = new CodeList<Instruction>();
		    ByteInputStream byteStream = new ByteInputStream(reader,reader.findInt());
		    int startOffset = byteStream.getByteCount();
		    Stack<Variable<?>> stack = new Stack<Variable<?>>();
		    try{
		    	while(true){
		    		long offset = byteStream.getByteCount() - startOffset + 1;
		    		List<Instruction> c = BytecodeParser.parse(byteStream, offset, constants, stack);
		    		if(c!=null)code.addAll(c);
		    	}
		    }catch(EOFException exception){
		    	// Expected
		    }catch(IOException unexpected){
		    	unexpected.printStackTrace();
		    	System.exit(-1);
		    }
		    
		    BytecodeNormalization.normalize(code);
		    methodsInstructions.add(code);
		    
		    int tablelength = reader.findShort();
		    
		    for(int i = 0; i < tablelength; i++){
		      int from = reader.findShort();
		      int to = reader.findShort();
		      int jump = reader.findShort();
		      int exception = reader.findShort();
		      if(exception == 0)System.out.println(String.format( "Handle all exception from [%s] to [%s] then jump to [%S]",from,to,jump ));
		      else System.out.println(String.format( "Handle exception [%s] from [%s] to [%s] then jump to [%S]", exception,from,to,jump ));
		    }

		    findAttributes( reader );
		}else{
			try {
				while (true)
					reader.findNext();
			} catch (EOFException exception) {
				// Expected
			}
		}
	}
	
	public String toString() {
		String out = "";
		out += "-------- Details --------\n";
		out += String.format("Minor version [%s]\n", minorVersion);
		out += String.format("Mayor version [%s]\n", majorVersion);
		out += String.format("Class access flag  [%s]\n", accessFlags);
		out += String.format("Class this [%s]\n", thisClass);
		out += String.format("Class super [%s]\n", superClass);
		for (int i : interfaces) {
			out += String.format("Class has interface [%s]\n", interfaces[i]);
		}
		for (int i = 0; i < fieldsName.length; i++) {
			out += String.format("Found field [%s] of type [%s]\n",
					fieldsName[i], fieldsType[i]);
		}
		out += "\n-------- Constants --------\n";
		for(Integer key : constants.keySet()){
			out += key+": "+constants.get(key)+"\n";
		}
		for (int i = 0; i < methodsName.length; i++) {
			out += "\n-------- Code --------\n";
			out += toStringMethod(i)+"\n";
			CodeList<Instruction> code = methodsInstructions.get(i);
			if(code != null)out += toStringIndentedCode(code);
		}
		return out;
	}
	
	public static String toStringIndentedCode(CodeList<Instruction> operations){
		String out = "";
		Stack<Instruction> stack = new Stack<Instruction>();
		for(Instruction i : operations){
			String tabs = "";for(int t = 0; t < stack.size(); t++)tabs+="\t";
			if(i.getOperation().equals(Instruction.START_INSTRUCTION)){
				stack.push(i);
			}else if(i.getOperation().equals(Instruction.END_INSTRUCTION)){
				if(tabs.length() > 0){
					tabs = tabs.substring(1);
					Instruction p = stack.pop();
					if(!i.getParam(-1).equals(p.getParam(-1))){
						//throw new RuntimeException();
					}
					
				}
			}
			out += tabs + i + "\n";
		}
		return out;
	}
	
	public String toStringMethod(int method){
		return String.format("Found method [%s] of type [%s]",
				methodsName[method], methodsType[method]);
	}
}
