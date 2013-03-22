package com.github.fru.torsion.utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import com.github.fru.torsion.utils.CodeList.Pointer;
import com.github.fru.torsion.utils.Instruction.Variable;


public class Main {
	
	public static void main(String[] args) {
		for(int i = 0; i < 5600; i++){
			main2(args);
		}
	}
	
	public static void main2(String[] args) {
		OrderStructure<String> test = new OrderStructure<String>();
		for(int i = 0 ; i < 10; i++){
			test.add(""+i);
		}

		Random random = new Random();
		CodeList<Instruction> operations = new CodeList<Instruction>();
		int count = 17;
		
		for(int i = 0; i < 1; i++){
			int first = random.nextInt(operations.size()+1);
			int second = random.nextInt(operations.size()+1-first);
			
			Variable v = new Variable();
			Pointer<Instruction> istart = operations.addPointer(first, new Instruction(v,"start"));
			Pointer<Instruction> igoto = operations.addPointer(first+second+1, new Instruction(v,"goto"));
			
			istart.getData().setReference(igoto);
			igoto.getData().setReference(istart);
		}
		for(int i = 0; i < count; i++){
			int first = random.nextInt(operations.size()+1);
			int second = random.nextInt(operations.size()+1-first);
			
			Variable v = new Variable();
			Pointer<Instruction> igoto = operations.addPointer(first, new Instruction(v,"goto"));
			Pointer<Instruction> iend = operations.addPointer(first+second+1, new Instruction(v,"end"));
			
			iend.getData().setReference(igoto);
			igoto.getData().setReference(iend);
		}
		
		/*
		for(Instruction i : operations){
			System.out.println(i);
		}
		
		System.out.println("---");
		*/
		OrderStructure<Pointer<Instruction>> order;
		
		
		order = new OrderStructure<Pointer<Instruction>>();
		for(Pointer<Instruction> istart : operations.reversePointer()){
			order.add(istart);
			Pointer<Instruction> igoto = istart.getData().getReference();
			if(istart.getData().getOperation().equals("start") && igoto.getData().getOperation().equals("goto")){
				Pointer<Instruction> lastInteresting = igoto;
				for(Pointer<Instruction> inbetweenStart : order.inbetween(igoto, istart)){
					if(inbetweenStart.getData().getOperation().equals("start")){ //&& inbetweenStart.getData().getReference().getData().getOperation().equals("end")
						Pointer<Instruction> inbetweeenEnd = inbetweenStart.getData().getReference();
						if(order.isAfter(inbetweeenEnd, lastInteresting)){
							lastInteresting = inbetweeenEnd;
						}
					}
				}
				Pointer<Instruction> iend = lastInteresting.addAfter(operations, new Instruction(istart.getData().getOutput(), "end"));
				order.addAfter(lastInteresting, iend);
				iend.getData().setReference(istart);
				istart.getData().setReference(iend);
			}
		}
		
		order = new OrderStructure<Pointer<Instruction>>();
		
		ArrayList<Pointer<Instruction>> pointerClone = new ArrayList<Pointer<Instruction>>();
		for(Pointer<Instruction> i : operations.getPointer()){
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
				
				for(Pointer<Instruction> splitStart : splitInteresting){
					Pointer<Instruction> splitEnd = splitStart.getData().getReference();
					if(!order.isAfter(lastInteresting, splitEnd)){
						
						Pointer<Instruction> newStart = splitStart.addAfter(operations, new Instruction(close, "start"));
						iend.getData().setReference(newStart);
						newStart.getData().setReference(iend);
						order.addBefore(splitStart, newStart);
						
						
						Pointer<Instruction> newGoto = newStart.addAfter(operations, new Instruction(close, "goto"));
						newGoto.getData().setReference(iend);
						order.addBefore(newStart, newGoto);
						

						iend = splitStart.addBefore(operations, new Instruction(close, "end"));
						order.addAfter(splitStart, iend);
					}
				}
				
				Pointer<Instruction> istart = lastInteresting.addBefore(operations, new Instruction(close, "start"));
				order.addAfter(lastInteresting, istart);
				iend.getData().setReference(istart);
				istart.getData().setReference(iend);
				
			}
		}
		
		printIndentedCode(operations);
		
	}
	
	
	public static void printIndentedCode(CodeList<Instruction> operations){
		Stack<Instruction> stack = new Stack<Instruction>();
		for(Instruction i : operations){
			String tabs = "";for(int t = 0; t < stack.size(); t++)tabs+="\t";
			if(i.getOperation().equals("start")){
				stack.push(i);
			}else if(i.getOperation().equals("end")){
				if(tabs.length() > 0){
					tabs = tabs.substring(1);
					Instruction p = stack.pop();
					if(!i.getOutput().equals(p.getOutput())){
						throw new RuntimeException();
					}
					
				}
			}
			System.out.println(tabs + i);
		}
	}
}
