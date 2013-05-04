package com.github.fru.torsion.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.github.fru.torsion.bytecode.utils.CodeList;
import com.github.fru.torsion.bytecode.utils.CodeList.Pointer;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.OrderStructure;
import com.github.fru.torsion.bytecode.utils.Type;
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
		HashMap<Variable<?>, Pointer<Instruction>> lookup = new HashMap<Variable<?>, Pointer<Instruction>>();
		for (Pointer<Instruction> current : method.getPointer()) {
			if (":".equals(current.getData().getOperation())) {
				lookup.put(current.getData().getParam(0), current);
			}
		}
		for (Pointer<Instruction> current : method.getPointer()) {
			if (Instruction.GOTO_INSTRUCTION.equals(current.getData().getOperation())) {
				if (current.getData().paramCount() < 2) continue;

				Pointer<Instruction> label = lookup.get(current.getData().getParam(1));
				if (label == null) continue;
				Variable<?> identifier = new Variable.Default(Type.LOCATION);
				current.getData().clear();
				Pointer<Instruction> newLabel;
				if (method.indexOf(label.getData()) > method.indexOf(current.getData())) { 
					// jump forward
					newLabel = label.addAfter(method, new Instruction(Instruction.END_INSTRUCTION).add(identifier));
				} else { 
					// jump backwards
					newLabel = label.addAfter(method, new Instruction(Instruction.START_INSTRUCTION).add(identifier));
				}
				setReference(current.getData(),newLabel);
				setReference(newLabel.getData(),current);
			}
		}
	}
	
	private static Pointer<Instruction> getReference(Instruction i){
		if(! (i.getParam(1).getValue() instanceof Pointer<?>))return null;
		@SuppressWarnings("unchecked")
		Variable<Pointer<Instruction>> out = (Variable<Pointer<Instruction>>) i.getParam(1);
		return out.getValue();
	}
	
	private static void setReference(Instruction i, Pointer<Instruction> pointer){
		i.setParam(1, new Variable<Pointer<Instruction>>(pointer, Type.REFERENCE));
	}

	private static void step2(CodeList<Instruction> method) {
		OrderStructure<Pointer<Instruction>> order = new OrderStructure<Pointer<Instruction>>();
		for (Pointer<Instruction> istart : method.reversePointer()) {
			order.add(istart);
			Pointer<Instruction> igoto = getReference(istart.getData());
			if (igoto != null && istart.getData().getOperation().equals(Instruction.START_INSTRUCTION) && igoto.getData().getOperation().equals(Instruction.GOTO_INSTRUCTION)) {
				Pointer<Instruction> lastInteresting = igoto;
				for (Pointer<Instruction> inbetweenStart : order.inbetween(igoto, istart)) {
					if (inbetweenStart.getData().getOperation().equals(Instruction.START_INSTRUCTION)) { 
						// && inbetweenStart.getData().getReference().getData().getOperation().equals("end")
						Pointer<Instruction> inbetweeenEnd = getReference(inbetweenStart.getData());
						if (order.isAfter(inbetweeenEnd, lastInteresting)) {
							lastInteresting = inbetweeenEnd;
						}
					}
				}
				Pointer<Instruction> iend = lastInteresting.addAfter(method, new Instruction(Instruction.END_INSTRUCTION).add(istart.getData().getParam(-1)));
				order.addAfter(lastInteresting, iend);
				setReference(iend.getData(),istart);
				setReference(istart.getData(),iend);
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
			Pointer<Instruction> igoto = getReference(iend.getData());
			if (iend.getData().getOperation().equals(Instruction.END_INSTRUCTION) && igoto.getData().getOperation().equals(Instruction.GOTO_INSTRUCTION)) {
				Pointer<Instruction> lastInteresting = igoto;
				ArrayList<Pointer<Instruction>> splitInteresting = new ArrayList<Pointer<Instruction>>();
				for (Pointer<Instruction> inbetween : order.inbetween(igoto, iend)) {
					if (inbetween.getData().getOperation().equals(Instruction.END_INSTRUCTION)) {
						Pointer<Instruction> inbetweenStart = getReference(inbetween.getData());
						if (order.isAfter(inbetweenStart, lastInteresting)) {
							lastInteresting = inbetweenStart;
						}
					}
					if (inbetween.getData().getOperation().equals(Instruction.START_INSTRUCTION)) {
						splitInteresting.add(0, inbetween);
					}
				}

				Variable<?> close = iend.getData().getParam(-1);

				Variable<?> splitVariable = null;

				for (Pointer<Instruction> splitStart : splitInteresting) {
					Pointer<Instruction> splitEnd = getReference(splitStart.getData());
					if (!order.isAfter(lastInteresting, splitEnd)) {

						if (splitVariable == null && igoto.getData().paramCount() > 0) {
							splitVariable = new Variable.Default(Type.BOOLEAN);
							Variable<?> constTrue = new Variable<Boolean>(true, new Type.Constant(Type.BOOLEAN)); 
							iend.addAfter(method, new Instruction("=").add(constTrue).add(splitVariable));
							igoto.addBefore(method, new Instruction("=").add(igoto.getData().getParam(0)).add(splitVariable));
							//igoto.getData().setOp(0, splitVariable);
						}

						Pointer<Instruction> newStart = splitStart.addAfter(method, new Instruction(Instruction.START_INSTRUCTION).add(close));
						setReference(iend.getData(),newStart);
						setReference(newStart.getData(),iend);
						order.addBefore(splitStart, newStart);

						Pointer<Instruction> newGoto = newStart.addAfter(method, new Instruction(Instruction.GOTO_INSTRUCTION/*,close*/));
						//newGoto.getData().add("<pointer>",splitVariable);
						setReference(newGoto.getData(),iend);
						order.addBefore(newStart, newGoto);

						iend = splitStart.addBefore(method, new Instruction(Instruction.END_INSTRUCTION).add(close));
						order.addAfter(splitStart, iend);
					}
				}

				Pointer<Instruction> istart = lastInteresting.addBefore(method, new Instruction(Instruction.START_INSTRUCTION).add(close));
				order.addAfter(lastInteresting, istart);
				setReference(iend.getData(),istart);
				setReference(istart.getData(),iend);
			}
		}
	}
}
