package com.github.fru.torsion.javascript;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.List;

import com.github.fru.torsion.bytecode.normalization.Block;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;
import com.github.fru.torsion.bytecode.normalization.Instruction.Jump;

public abstract class JsWriterInstruction implements JsWriter{
	
	protected abstract String getLocal(AccessibleObject parent, Identifier id);
	protected abstract String getName(AnnotatedElement annotated);
	
	private void fillIdentifiers(LinkedHashSet<Identifier> identifiers, List<Instruction> instructions){
		for(Instruction ins : instructions){
			if(ins instanceof Block)fillIdentifiers(identifiers, ((Block)ins).body);
			for(Identifier i : ins.getParameter()){
				if(i.id != null)identifiers.add(i);
			}
		}
	}

	@Override
	public void writeAccessible(PrintWriter out, JsWriterModule defaultWriter, AccessibleObject accessible, Body body){
		boolean isStatic = accessible instanceof Method && Modifier.isStatic(((Method)accessible).getModifiers());
		LinkedHashSet<Identifier> identifiers = new LinkedHashSet<Identifier>();
		fillIdentifiers(identifiers, body.body);
		if(identifiers.size() > 0){
			out.print("var ");
			int j = 0;
			Object v0 = new Identifier.LocalVariable(0);
			for(Identifier i : identifiers){
				j += 1;
				out.print(this.getLocal(accessible, i));
				if(i.type.isConstant()){
					out.print("=");
					out.print(i.type.getConstantValue());
				}
				if(!isStatic && v0.equals(i.id)){
					out.print("=this");
				}
				
				if(identifiers.size() > j){
					out.print(",");
					if(j % 20 == 0)out.println();
				}
			}
			out.println(";");
		}
		for(Instruction i : body.body){
			allInstruction(out, i, accessible, defaultWriter);
		}
	}
	
	private void allInstruction(PrintWriter out, Instruction i, AccessibleObject accessible, JsWriterModule defaultWriter){
		if(i instanceof Block){
			out.print("label_");
			out.print(i.location);
			out.println(":while(true){");
			for(Instruction i2 : ((Block)i).body){
				allInstruction(out, i2, accessible, defaultWriter);
			}
			out.println("break;");
			out.println("}");
		}
		if(i instanceof Jump && !((Jump)i).isForward()){
			Jump j = (Jump)i;
			if(j.getParameter().size() > 0){
				out.print("if(");
				out.print(getLocal(accessible, j.getParameter().get(0)));
				out.print("){");
			}
			out.print("continue label_");
			out.print(j.target);
			out.print(";");
			if(j.getParameter().size() > 0){
				out.print("}");
			}
			out.println();
		}
		
		if(i.getParameter().size() >= 2){
			Member member = (Member)i.getParameter().get(1).accessible;
			if(member != null){
				if(JsWriter.class.isAssignableFrom(member.getDeclaringClass())){
					try {
						JsWriter w = (JsWriter) member.getDeclaringClass().newInstance();
						w.writeInvocation(out, defaultWriter, accessible, i.getParameter().get(1).accessible, 
								i.getParameter().toArray(new Identifier[0]));
					} catch (Exception e) {
						
					}
				}else{
					defaultWriter.writeInvocation(out, defaultWriter, accessible, i.getParameter().get(1).accessible, 
							i.getParameter().toArray(new Identifier[0]));
				}
			}
		}
		simpleInstruction(out, i, accessible);
	}
	
	private void simpleInstruction(PrintWriter out, Instruction i, AccessibleObject accessible){
		String o = i.operation;
		if(o.equals("return")){
			if(i.getParameter().size()>0){
				out.print("return ");
				out.print(this.getLocal(accessible, i.getParameter().get(0)));
				out.println(";");
			}else{
				out.println("return;");
			}
		}else if(o.equals("=")){
			out.print(this.getLocal(accessible, i.getParameter().get(0)));
			out.print("=");
			out.print(this.getLocal(accessible, i.getParameter().get(1)));
			out.println(";");
		}else if(o.equals("+") || o.equals("-") || o.equals("/") || o.equals("*") || o.equals(">") || o.equals("<") || o.equals("%")){
			out.print(this.getLocal(accessible, i.getParameter().get(0)));
			out.print("=");
			out.print(this.getLocal(accessible, i.getParameter().get(1)));
			out.print(i.operation);
			out.print(this.getLocal(accessible, i.getParameter().get(2)));
			out.println(";");
		}
	}
}
