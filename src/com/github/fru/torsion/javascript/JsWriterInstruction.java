package com.github.fru.torsion.javascript;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
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
	
	protected abstract void replace(Identifier identifier);
	private void fillIdentifiers(LinkedHashSet<Identifier> identifiers, LinkedHashSet<Integer> targets, List<Instruction> instructions){
		for(Instruction ins : instructions){
			if(ins instanceof Jump && ((Jump)ins).isForward()){
				targets.add(((Jump)ins).target);
			}
			if(ins instanceof Block)fillIdentifiers(identifiers, targets, ((Block)ins).body);
			for(Identifier i : ins.getParameter()){
				replace(i);
				if(i.id != null)identifiers.add(i);
			}
		}
	}

	LinkedHashSet<Integer> targets; 
	
	@Override
	public void writeAccessible(PrintWriter out, JsWriterModule defaultWriter, AccessibleObject accessible, Body body){
		boolean isStatic = accessible instanceof Method && Modifier.isStatic(((Method)accessible).getModifiers());
		LinkedHashSet<Identifier> identifiers = new LinkedHashSet<Identifier>();
		targets = new LinkedHashSet<Integer>();
		fillIdentifiers(identifiers, targets, body.body);
		if(identifiers.size() > 0){
			out.print("var ");
			boolean first = true;
			for(Integer i : targets){
				if(!first)out.print(",");
				first = false;
				out.print("cond_");
				out.print(i);
				out.print("=false");
			}
			for(Identifier i : identifiers){
				if(!first)out.print(",");
				first = false;
				out.print(this.getLocal(accessible, i));
				if(i.type.isConstant()){
					out.print("=");
					out.print(i.type.getConstantValue());
				}
				if(i.id instanceof Identifier.LocalVariable){
					Class<?>[] p = (accessible instanceof Method)?((Method)accessible).getParameterTypes():((Constructor<?>)accessible).getParameterTypes();
					int n = ((Identifier.LocalVariable)i.id).local;
					if(!isStatic)n -= 1;
					if(p.length>n){
						if(n == -1)out.print("=this");
						else out.print("=a_"+n);
					}
				}
			}
			out.println(";");
		}
		for(Instruction i : body.body){
			allInstruction(out, i, accessible, defaultWriter);
		}
	}
	
	private void allInstruction(PrintWriter out, Instruction i, AccessibleObject accessible, JsWriterModule defaultWriter){
		if(targets != null && targets.contains(i.location) && !(i instanceof Block)){
			out.print("cond_");
			out.print(i.location);
			out.println("=false;");
			targets.remove(i.location);
		}
		
		if(i instanceof Block){
			Block b = ((Block)i);
			if(b.forward){
				out.print("if(!cond_");
				out.print(b.location);
				out.println("){");
				for(Instruction i2 : b.body){
					allInstruction(out, i2, accessible, defaultWriter);
				}
				out.println("}");
			}else{
				out.print("label_");
				out.print(i.location);
				out.println(":while(true){");
				for(Instruction i2 : b.body){
					allInstruction(out, i2, accessible, defaultWriter);
				}
				out.println("break;");
				out.println("}");
			}
		}
		if(i instanceof Jump){
			Jump j = (Jump)i;
			if(j.isForward()){
				out.print("cond_");
				out.print(j.target);
				out.print("=");
				out.print(j.getParameter().size() == 0 ? "true" : getLocal(accessible, j.getParameter().get(0)));
				out.println(";");
			}else{
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
		}
		
		if(i.getParameter().size() >= 2 && i.getParameter().get(1).accessible != null){
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
		}else if(i.getParameter().size() >= 1 && i.getParameter().get(0).accessible != null){
			Member member = (Member)i.getParameter().get(0).accessible;
			if(member != null){
				if(JsWriter.class.isAssignableFrom(member.getDeclaringClass())){
					try {
						JsWriter w = (JsWriter) member.getDeclaringClass().newInstance();
						w.writeInvocation(out, defaultWriter, accessible, i.getParameter().get(0).accessible, 
								i.getParameter().toArray(new Identifier[0]));
					} catch (Exception e) {
					}
				}else{
					defaultWriter.writeInvocation(out, defaultWriter, accessible, i.getParameter().get(0).accessible, 
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
		}else if(o.equals("+") || o.equals("-") || o.equals("/") || o.equals("*") || 
				o.equals(">") || o.equals("<") || o.equals("%") || o.equals(">=") || o.equals("<=")){
			out.print(this.getLocal(accessible, i.getParameter().get(0)));
			out.print("=");
			out.print(this.getLocal(accessible, i.getParameter().get(1)));
			out.print(i.operation);
			out.print(this.getLocal(accessible, i.getParameter().get(2)));
			out.println(";");
		}else if(o.equals("==") || o.equals("!=") ){
			out.print(this.getLocal(accessible, i.getParameter().get(0)));
			out.print("=");
			out.print(this.getLocal(accessible, i.getParameter().get(1)));
			out.print(i.operation+"=");
			out.print(this.getLocal(accessible, i.getParameter().get(2)));
			out.println(";");
		}else if(o.equals("new") && i.getParameter().size() > 0){
			Class<?> clazz = i.getParameter().get(0).type.getClasses().get(0);
			out.print(getLocal(accessible, i.getParameter().get(0)));
			out.print("=new ");
			out.print(getName(clazz));
			out.println("();");
		}
	}
}
