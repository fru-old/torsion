package com.github.fru.torsion.javascript;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;

import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;

public abstract class JsWriterInstruction implements JsWriter{
	
	protected abstract String getLocal(AccessibleObject parent, Identifier id);
	protected abstract String getName(AnnotatedElement annotated);

	public void writeAccessible(PrintWriter out, AccessibleObject accessible, Body body){
		boolean isStatic = accessible instanceof Method && Modifier.isStatic(((Method)accessible).getModifiers());
		LinkedHashSet<Identifier> identifiers = new LinkedHashSet<Identifier>();
		for(Instruction ins : body.body){
			for(Identifier i : ins.getParameter()){
				if(i.id != null)identifiers.add(i);
			}
		}
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
			simpleInstruction(out, i, accessible);
		}
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
		}else if(o.equals("+") || o.equals("-")){
			out.print(this.getLocal(accessible, i.getParameter().get(0)));
			out.print("=");
			out.print(this.getLocal(accessible, i.getParameter().get(1)));
			out.print(i.operation);
			out.print(this.getLocal(accessible, i.getParameter().get(2)));
			out.println(";");
		}
	}
}
