package com.github.fru.torsion.writer.normalization;

import java.util.HashMap;

public class Operation {
	
	public static enum Type{
		NOOP, GOTO_BACKWARD, GOTO_FORWARD, BLOCK_BACKWARD, BLOCK_FORWARD, OTHER;
	}
	
	public final Type identifier;
	public final int location;
	
	public Operation(Type identifier, int location){
		this.identifier = identifier;
		this.location = location;
	}
	
	public Operation(Type identifier){
		 this(identifier,-1);
	}

	public boolean isType(Type type){
		return identifier == type;
	}
	
	//Following depends strongly on the operation type
	
	private int jump = -1;
	public Operation setJump(int jump){
		this.jump = jump;
		return this;
	}
	
	@Override
	public String toString(){
		StringBuilder out = new StringBuilder();
		out.append(identifier.toString());
		if(identifier == Type.NOOP){
			out.append(" ");
			out.append(location);
		}else if(identifier == Type.GOTO_BACKWARD || identifier == Type.GOTO_FORWARD){
			out.append(" ");
			if(jumpOperation == null) out.append(jump);
			else out.append(jumpOperation.toString());
		}
		return out.toString();
	}
	
	//Normalization
	
	protected Operation jumpOperation = null;
	public static void assignJumpOperation(Method method){
		HashMap<Integer,Operation> map = new HashMap<Integer, Operation>();
		for(Operation op : method.body){
			if( op.location > -1 && !map.containsKey(op.location)){
				map.put(op.location, op);
			}
		}
		for(Operation op : method.body){
			if(op.identifier == Type.GOTO_BACKWARD || op.identifier == Type.GOTO_FORWARD){
				if(map.containsKey(op.jump)){
					op.jumpOperation = map.get(op.jump);
				}
			}
		}
	}
}
