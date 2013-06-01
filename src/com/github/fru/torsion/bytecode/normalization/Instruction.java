package com.github.fru.torsion.bytecode.normalization;

import java.util.ArrayList;

public class Instruction {

	public final int location;
	public final String operation;
	
	public Instruction(int location){
		this(location,"");
	}
	
	public Instruction(int location, String operation){
		this.location = location;
		this.operation = operation;
	}
	
	private final ArrayList<Identifier> parameter = new ArrayList<Identifier>();
	
	public String toString(){
		if(parameter.size() > 0){
			StringBuilder out = new StringBuilder();
			out.append(operation);
			out.append("( ");
			for(Identifier id : parameter){
				out.append(id.toStringAndType());
				out.append(", ");
			}
			out.append(")");
			return out.toString();
		}else if(!operation.equals("")){
			return operation;
		}
		return "NOOP "+location;
	}
	
	public Instruction add(Identifier identfier){
		parameter.add(identfier);
		return this;
	}
	
	public static class Jump extends Instruction {
		
		public final int target;
		public Jump(int location, int target){
			super(location);
			this.target = target;
		}
		
		public boolean isForward(){
			return location < target;
		}
		
		public String toString(){
			return location+": Goto_" + (isForward()?"Forward":"Backward") + " " + target;
		}
		
		private Block block;
		public Block getBlock(){
			return block;
		}
		public void setBlock(Block block){
			this.block = block;
		}
	}
}
