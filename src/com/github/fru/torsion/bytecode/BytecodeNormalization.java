package com.github.fru.torsion.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.github.fru.torsion.bytecode.utils.CodeList;
import com.github.fru.torsion.bytecode.utils.CodeList.Pointer;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.OrderStructure;
import com.github.fru.torsion.bytecode.utils.Variable;

public class BytecodeNormalization {

	public static void normalize(CodeList<Instruction> method) {
		// Add start and end instructions for goto's
		BytecodeNormalization.step1(method);
		// Remove unnesecarry labels
		for (Iterator<Pointer<Instruction>> i = method.getPointer().iterator(); i.hasNext();) {
			Pointer<Instruction> current = i.next();
			if (":".equals(current.getData().getOperation())) {
				i.remove();
			}
		}

		BytecodeNormalization.step2(method);
		BytecodeNormalization.step3(method);
		//BytecodeNormalization.step4(method);
	}

	private static void step1(CodeList<Instruction> method) {
		HashMap<Variable, Pointer<Instruction>> lookup = new HashMap<Variable, Pointer<Instruction>>();
		for (Pointer<Instruction> current : method.getPointer()) {
			if (":".equals(current.getData().getOperation())) {
				lookup.put(current.getData().getOp(0), current);
			}
		}
		for (Pointer<Instruction> current : method.getPointer()) {
			if (Instruction.GOTO_INSTRUCTION.equals(current.getData().getOperation())) {
				if (current.getData().paramCount() < 2) continue;
				if (current.getData().getOp(1) == Variable.END) continue;

				Pointer<Instruction> label = lookup.get(current.getData().getOp(1));
				if (label == null) continue;
				Variable identifier = new Variable();
				current.getData().clear();
				Pointer<Instruction> newLabel;
				if (method.indexOf(label.getData()) > method.indexOf(current.getData())) { 
					// jump forward
					newLabel = label.addAfter(method, new Instruction(Instruction.END_INSTRUCTION,identifier));
				} else { 
					// jump backwards
					newLabel = label.addAfter(method, new Instruction(Instruction.START_INSTRUCTION,identifier));
				}
				current.getData().setReference(newLabel);
				newLabel.getData().setReference(current);
			}
		}
	}

	private static void step2(CodeList<Instruction> method) {
		OrderStructure<Pointer<Instruction>> order = new OrderStructure<Pointer<Instruction>>();
		for (Pointer<Instruction> istart : method.reversePointer()) {
			order.add(istart);
			Pointer<Instruction> igoto = istart.getData().getReference();
			if (igoto != null && istart.getData().getOperation().equals(Instruction.START_INSTRUCTION) && igoto.getData().getOperation().equals(Instruction.GOTO_INSTRUCTION)) {
				Pointer<Instruction> lastInteresting = igoto;
				for (Pointer<Instruction> inbetweenStart : order.inbetween(igoto, istart)) {
					if (inbetweenStart.getData().getOperation().equals(Instruction.START_INSTRUCTION)) { 
						// && inbetweenStart.getData().getReference().getData().getOperation().equals("end")
						Pointer<Instruction> inbetweeenEnd = inbetweenStart.getData().getReference();
						if (order.isAfter(inbetweeenEnd, lastInteresting)) {
							lastInteresting = inbetweeenEnd;
						}
					}
				}
				Pointer<Instruction> iend = lastInteresting.addAfter(method, new Instruction(Instruction.END_INSTRUCTION,istart.getData().getOp(-1)));
				order.addAfter(lastInteresting, iend);
				iend.getData().setReference(istart);
				istart.getData().setReference(iend);
			}
		}
	}

	private static void step3(CodeList<Instruction> method) {
		OrderStructure<Pointer<Instruction>> order = new OrderStructure<Pointer<Instruction>>();
		ArrayList<Pointer<Instruction>> pointerClone = new ArrayList<Pointer<Instruction>>();
		for (Pointer<Instruction> i : method.getPointer()) {
			pointerClone.add(i);
		}

		for (Pointer<Instruction> iend : pointerClone) {
			order.add(iend);
			Pointer<Instruction> igoto = iend.getData().getReference();
			if (iend.getData().getOperation().equals(Instruction.END_INSTRUCTION) && igoto.getData().getOperation().equals(Instruction.GOTO_INSTRUCTION)) {
				Pointer<Instruction> lastInteresting = igoto;
				ArrayList<Pointer<Instruction>> splitInteresting = new ArrayList<Pointer<Instruction>>();
				for (Pointer<Instruction> inbetween : order.inbetween(igoto, iend)) {
					if (inbetween.getData().getOperation().equals(Instruction.END_INSTRUCTION)) {
						Pointer<Instruction> inbetweenStart = inbetween.getData().getReference();
						if (order.isAfter(inbetweenStart, lastInteresting)) {
							lastInteresting = inbetweenStart;
						}
					}
					if (inbetween.getData().getOperation().equals(Instruction.START_INSTRUCTION)) {
						splitInteresting.add(0, inbetween);
					}
				}

				Variable close = iend.getData().getOp(-1);

				Variable splitVariable = null;

				for (Pointer<Instruction> splitStart : splitInteresting) {
					Pointer<Instruction> splitEnd = splitStart.getData().getReference();
					if (!order.isAfter(lastInteresting, splitEnd)) {

						if (splitVariable == null && igoto.getData().paramCount() > 0) {
							splitVariable = new Variable();
							iend.addAfter(method, new Instruction("=", "true", splitVariable));
							igoto.addBefore(method, new Instruction("=", igoto.getData().getOp(0), splitVariable));
							//igoto.getData().setOp(0, splitVariable);
						}

						Pointer<Instruction> newStart = splitStart.addAfter(method, new Instruction(Instruction.START_INSTRUCTION,close));
						iend.getData().setReference(newStart);
						newStart.getData().setReference(iend);
						order.addBefore(splitStart, newStart);

						Pointer<Instruction> newGoto = newStart.addAfter(method, new Instruction(Instruction.GOTO_INSTRUCTION/*,close*/));
						//newGoto.getData().add("<pointer>",splitVariable);
						newGoto.getData().setReference(iend);
						order.addBefore(newStart, newGoto);

						iend = splitStart.addBefore(method, new Instruction(Instruction.END_INSTRUCTION,close));
						order.addAfter(splitStart, iend);
					}
				}

				Pointer<Instruction> istart = lastInteresting.addBefore(method, new Instruction(Instruction.START_INSTRUCTION,close));
				order.addAfter(lastInteresting, istart);
				iend.getData().setReference(istart);
				istart.getData().setReference(iend);

			}
		}
	}

	/*private static void step4(CodeList<Instruction> method) {
		Stack<Variable> stack = new Stack<Variable>();
		for (Instruction i : method) {
			for (int input = 0; input < i.paramCount()-1; input++) {
				if (i.getOp(input) != null && i.getOp(input).isStack()) {
					if (stack.size() > 0) {
						i.setOp(input, stack.pop());
					}
				}
			}
			if (i.getOp(-1) != null && i.getOp(-1).isStack()) {
				i.setOp(-1,new Variable());
				stack.push(i.getOp(-1));
			}
		}
	}*/
}
