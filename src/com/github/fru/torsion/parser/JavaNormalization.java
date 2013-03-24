package com.github.fru.torsion.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import com.github.fru.torsion.utils.CodeList;
import com.github.fru.torsion.utils.OrderStructure;
import com.github.fru.torsion.utils.CodeList.Pointer;
import com.github.fru.torsion.utils.Instruction;
import com.github.fru.torsion.utils.Instruction.Variable;

public class JavaNormalization {

	public static void normalize(CodeList<Instruction> method){
		//Add start and end instructions for goto's
		JavaNormalization.step1(method);
		//Remove unnesecarry labels
		for(Iterator<Pointer<Instruction>> i = method.getPointer().iterator(); i.hasNext();){
			Pointer<Instruction> current = i.next();
			if(":".equals(current.getData().getOperation())){
				i.remove();
			}
		}
		
		JavaNormalization.step2(method);
		JavaNormalization.step3(method);
		JavaNormalization.step4(method);
	}
	
	private static void step1(CodeList<Instruction> method){
		HashMap<Variable, Pointer<Instruction>> lookup = new HashMap<Variable, Pointer<Instruction>>();
		for(Pointer<Instruction> current : method.getPointer()){
			if(":".equals(current.getData().getOperation())){
				lookup.put(current.getData().getInputs()[0], current);
			}
		}
		for(Pointer<Instruction> current : method.getPointer()){
			if("goto".equals(current.getData().getOperation())){
				if(current.getData().getInputs() == null )continue;
				if(current.getData().getInputs().length < 2 )continue;
				if(current.getData().getInputs()[1] == Variable.END )continue;
				
				Pointer<Instruction> label = lookup.get(current.getData().getInputs()[1]);
				if(label == null)continue;
				Variable identifier = new Variable();
				current.getData().setOutput(identifier);
				current.getData().setInput(new Variable[0]);
				Pointer<Instruction> newLabel;
				if(method.indexOf(label.getData()) > method.indexOf(current.getData())){ //jump forward
					newLabel = label.addAfter(method, new Instruction(identifier, "end"));
					
				}else{ //jump backwards
					newLabel = label.addAfter(method, new Instruction(identifier, "start"));
				}
				current.getData().setReference(newLabel);
				newLabel.getData().setReference(current);
			}
		}
	}
	
	private static void step2(CodeList<Instruction> method){
		OrderStructure<Pointer<Instruction>> order = new OrderStructure<Pointer<Instruction>>();
		for(Pointer<Instruction> istart : method.reversePointer()){
			order.add(istart);
			Pointer<Instruction> igoto = istart.getData().getReference();
			if(igoto != null && istart.getData().getOperation().equals("start") && igoto.getData().getOperation().equals("goto")){
				Pointer<Instruction> lastInteresting = igoto;
				for(Pointer<Instruction> inbetweenStart : order.inbetween(igoto, istart)){
					if(inbetweenStart.getData().getOperation().equals("start")){ //&& inbetweenStart.getData().getReference().getData().getOperation().equals("end")
						Pointer<Instruction> inbetweeenEnd = inbetweenStart.getData().getReference();
						if(order.isAfter(inbetweeenEnd, lastInteresting)){
							lastInteresting = inbetweeenEnd;
						}
					}
				}
				Pointer<Instruction> iend = lastInteresting.addAfter(method, new Instruction(istart.getData().getOutput(), "end"));
				order.addAfter(lastInteresting, iend);
				iend.getData().setReference(istart);
				istart.getData().setReference(iend);
			}
		}
	}
	
	private static void step3(CodeList<Instruction> method){
		OrderStructure<Pointer<Instruction>> order = new OrderStructure<Pointer<Instruction>>();
		ArrayList<Pointer<Instruction>> pointerClone = new ArrayList<Pointer<Instruction>>();
		for(Pointer<Instruction> i : method.getPointer()){
			pointerClone.add(i);
		}
		
		for(Pointer<Instruction> iend : pointerClone){
			order.add(iend);
			Pointer<Instruction> igoto = iend.getData().getReference();
			if(iend.getData().getOperation().equals("end") && igoto.getData().getOperation().equals("goto")){
				Pointer<Instruction> lastInteresting = igoto;
				ArrayList<Pointer<Instruction>> splitInteresting = new ArrayList<Pointer<Instruction>>();
				for(Pointer<Instruction> inbetween : order.inbetween(igoto, iend)){
					if(inbetween.getData().getOperation().equals("end")){
						Pointer<Instruction> inbetweenStart = inbetween.getData().getReference();
						if(order.isAfter(inbetweenStart, lastInteresting)){
							lastInteresting = inbetweenStart;
						}
					}
					if(inbetween.getData().getOperation().equals("start")){
						splitInteresting.add(0,inbetween);
					}
				}
				
				Variable close = iend.getData().getOutput();
				
				Variable splitVariable = null;
				
				for(Pointer<Instruction> splitStart : splitInteresting){
					Pointer<Instruction> splitEnd = splitStart.getData().getReference();
					if(!order.isAfter(lastInteresting, splitEnd)){
						
						if(splitVariable == null){
							splitVariable = new Variable();
							iend.addAfter(method, new Instruction(splitVariable,"=","true"));
							igoto.addBefore(method, new Instruction(splitVariable,"=",igoto.getData().getInputs()[0]));
							igoto.getData().getInputs()[0] = splitVariable;
						}
						
						Pointer<Instruction> newStart = splitStart.addAfter(method, new Instruction(close, "start"));
						iend.getData().setReference(newStart);
						newStart.getData().setReference(iend);
						order.addBefore(splitStart, newStart);
						
						
						Pointer<Instruction> newGoto = newStart.addAfter(method, new Instruction(close, "goto"));
						newGoto.getData().setInput(new Variable[]{splitVariable});
						newGoto.getData().setReference(iend);
						order.addBefore(newStart, newGoto);
						

						iend = splitStart.addBefore(method, new Instruction(close, "end"));
						order.addAfter(splitStart, iend);
					}
				}
				
				Pointer<Instruction> istart = lastInteresting.addBefore(method, new Instruction(close, "start"));
				order.addAfter(lastInteresting, istart);
				iend.getData().setReference(istart);
				istart.getData().setReference(iend);
				
			}
		}
	}
	
	private static void step4(CodeList<Instruction> method){
		Stack<Variable> stack = new Stack<Variable>();
		for(Instruction i : method){
			for(int input = 0; input < i.getInputs().length; input++){
				if(i.getInputs()[input] != null && i.getInputs()[input].isStack()){
					if(stack.size() > 0){
						i.getInputs()[input] = stack.pop();
					}
				}
			}
			if(i.getOutput() != null && i.getOutput().isStack()){
				i.setOutput(new Variable());
				stack.push(i.getOutput());
			}
		}
	}	
}
