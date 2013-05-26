package com.github.fru.torsion.bytecode.normalization;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.parser.InvocationOperation;

public class MethodBody {

	public final ArrayList<Instruction> body = new ArrayList<Instruction>();
	public final HashMap<Identifier, Type> local = new HashMap<Identifier, Type>();
	
	public void parseBody(ByteInputStream reader, HashMap<Integer, ClassFileConstant> constants, Class<?> clazz) throws IOException{
	    ByteInputStream byteStream = new ByteInputStream(reader,reader.findInt());
	    int startOffset = byteStream.getByteCount();
	    
	    Stack<Identifier> stack = new Stack<Identifier>();
	    AbstractParser[] parsers = new AbstractParser[]{
	    		new InvocationOperation(stack, constants,  body, clazz),
	    };
	    try{
	    	while(true){
	    		int offset = byteStream.getByteCount() - startOffset + 1;
	    		body.add(new Instruction(offset));
	    		int bytecode = byteStream.findNext();
	    		for(AbstractParser parser : parsers){
	    			if(parser.isApplicable(bytecode)){
	    				parser.parse(bytecode, byteStream, offset);
	    				break;
	    			}
	    		}
	    	}
	    }catch(EOFException exception){
	    	// Expected
	    }
	}
	
	@Override
	public String toString(){ 
		StringBuilder out = new StringBuilder();
		for(Instruction i : body){
			out.append(i.toString());
			out.append('\n');
		}
		if(out.length()>0)out.setLength(out.length()-1);
		return out.toString();
	}
	
	public static abstract class AbstractParser{
		protected final Stack<Identifier> stack;
		protected final HashMap<Integer, ClassFileConstant> constants; 
		protected final ArrayList<Instruction> body;
		protected final  Class<?> clazz;
		
		public AbstractParser(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz){
			this.stack = stack;
			this.constants = constants;
			this.body = body;
			this.clazz = clazz;
		}
		
		public abstract void parse(int bytecode, ByteInputStream byteStream, int location) throws IOException;
		public abstract boolean isApplicable(int bytecode);
	}
}
