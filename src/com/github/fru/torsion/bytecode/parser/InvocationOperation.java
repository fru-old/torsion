package com.github.fru.torsion.bytecode.parser;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;

public class InvocationOperation extends Body.AbstractParser{
	
	public InvocationOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws IOException {
		ClassFileConstant constant = constants.get(byteStream.findShort());
		
		Identifier id;
		Identifier other;
		
		switch(bytecode){
		case 0xB2: //getstatic
		case 0xB3: //putstatic
		case 0xB4: //getfield
		case 0xB5:	//putfield
			String clazz = constants.get(constants.get(constant.getRef1()).getRef1()).getConstant();
			ClassFileConstant nameAndType = constants.get(constant.getRef2());
			ClassFileConstant name = constants.get(nameAndType.getRef1());
			try {
				id = new Identifier(name.getConstant(), Class.forName(clazz.replace('/', '.')));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			if(bytecode == 0xB2 || bytecode == 0xB4){
				stack.push(other = new Identifier());
				other.type.add(((Field)id.accessible).getType());
				body.add(new Instruction(location,"=field").add(other).add(id));
			}else{
				body.add(new Instruction(location,"=field").add(id).add(stack.pop()));
			}
			break;
		}
		
		switch(bytecode){
		case 0xB6:
		case 0xB7:
		case 0xB8:
		case 0xB9:
			String clazz = constants.get(constants.get(constant.getRef1()).getRef1()).getConstant();
			ClassFileConstant nameAndType = constants.get(constant.getRef2());
			String name = constants.get(nameAndType.getRef1()).getConstant();
			String signature = constants.get(nameAndType.getRef2()).getConstant();
			Identifier accessable = new Identifier(name, clazz, signature);
			Instruction i = new Instruction(location,bytecode==0xB7?"direct":"call");
			
			Class<?> result = Void.class;
			if(accessable.accessible instanceof Method){
				Method m = (Method)accessable.accessible;
				result = m.getReturnType();
			}
			
			Identifier r = null;
			if(result != Void.class){
				r = new Identifier();
				r.type.add(result);
				i.add(r);
			}
			i.add(accessable);
			
			int count = 0;
			if(accessable.accessible instanceof Method){
				count = ((Method)accessable.accessible).getParameterTypes().length;
			}else if(accessable.accessible instanceof Constructor<?>){
				count = ((Constructor<?>)accessable.accessible).getParameterTypes().length;
			}
			ArrayList<Identifier> parameter = new ArrayList<Identifier>();
			for(int j = 0; j < count; j++)parameter.add(stack.pop());
			if(bytecode != 0xB8)i.add(stack.pop()); //this
			for(int j = count-1; j >= 0; j--)i.add(parameter.get(j));
			
			
			
			if(r != null)stack.push(r);
			body.add(i);
			if(bytecode == 0xB9)byteStream.findShort();
			break;
		case 0xBA:
			byteStream.findShort();
			System.out.println("Invokedynamic wird leider nicht unterstützt!");
			break;
		}
		//TODO: Invocation need to parse types, to get operand count
	}

	@Override
	public boolean isApplicable(int bytecode) {
		if(0xB2 <= bytecode && bytecode <= 0xBB)return true;
		if(bytecode == 0xBD)return true;
		return false;
	}

}
