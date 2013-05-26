package com.github.fru.torsion.bytecode.normalization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Block extends Instruction{

	public final List<Instruction> body;
	public final boolean forward;
	
	public Block(int location, boolean forward){
		this(location, forward, new ArrayList<Instruction>());
	}
	
	public Block(int location, boolean forward, List<Instruction> body){
		super(location);
		this.body = body;
		this.forward = forward;
	}
	
	public String toString(){
		StringBuilder out = new StringBuilder();
		out.append(location);
		out.append(forward?"_F":"_B");
		out.append("{\n");
		for(Instruction op : body){
			out.append("    ");
			out.append(op.toString().replace("\n", "\n    "));
			out.append('\n');
		}
		out.append("}");
		return out.toString();
	}
	
	//Normalization
	
	public static void applyBlocks(List<Instruction> list){
		applySimpleBackwardBlocks(list);
		findForwardBlocks(list,list, new HashSet<Integer>());
	}
	
	private static void findForwardBlocks(List<Instruction> list,List<Instruction> top, HashSet<Integer> complete){
		for(int i = 0; i < list.size(); i++){
			Instruction o = list.get(i);
			if(o instanceof Block){
				findForwardBlocks(((Block)o).body, top, complete);
			}else if(isCorrectJump(o, true) && !complete.contains(((Jump)o).target)){
				applyForwardBlocks(top, ((Jump)o).target, false);
				complete.add(((Jump)o).target);
				i = -1;
			}
		}
	}
	
	private enum FoundStatus{
		NOTHING, JUMP, TARGET
	}
	
	private static FoundStatus applyForwardBlocks(List<Instruction> list, int label , boolean jumpIsAbove){
		List<Integer> jumpPositions = new ArrayList<Integer>();
		Integer targetPosition = null;
		
		int i = 0;
		for(Instruction o : list){
			if(o instanceof Block){
				FoundStatus status = applyForwardBlocks(((Block)o).body, label, jumpIsAbove || jumpPositions.size() > 0);
				if(status == FoundStatus.JUMP){
					jumpPositions.add(i);
				}else if(status == FoundStatus.TARGET && targetPosition == null){
					targetPosition = i-1;
				}
			}else if(isCorrectJump(o, true) && ((Jump)o).target == label){
				jumpPositions.add(i);
			}else if(o.location == label && targetPosition == null){
				targetPosition = i-1;
			}
			
			if(targetPosition != null)break;
			i++;
		}
		
		if((targetPosition != null && jumpIsAbove) || jumpPositions.size() > 0){
			int target = targetPosition == null ? list.size() : targetPosition + 1;
			if(jumpPositions.size() == 0 || (jumpIsAbove && targetPosition != null))jumpPositions.add(0,-1);
			int reduce = 0;
			for(int j = jumpPositions.size()-1; j >= 0; j--){
				int begin = jumpPositions.get(j) + 1;
				if(begin < target){
					createBlock(list, begin, target-reduce, label, true);
					reduce += (target - begin) -1;
				}
				
			}
		}

		if(targetPosition != null)return FoundStatus.TARGET;
		if(jumpPositions.size() > 0)return FoundStatus.JUMP;
		return FoundStatus.NOTHING;
	}
	
	private static Block createBlock(List<Instruction> parent, int start, int end, int location, boolean forward){
		ArrayList<Instruction> body = new ArrayList<Instruction>();
		Block b = new Block(location, forward, body);
		for(int i  = start; i < end; i++){	
			body.add(parent.get(start));
			parent.remove(start);
		}
		parent.add(start,b);
		return b;
	}
	
	private static void setJumpBlock(List<Instruction> list, int location, Block block, boolean forward){
		for(Instruction o : list){
			if(o instanceof Block)setJumpBlock(((Block)o).body, location, block, forward);
			if(isCorrectJump(o, forward)){
				Jump jump = (Jump) o;
				if(jump.target == location){
					jump.setBlock(block);
				}
			}
		}
	}
	
	private static void applySimpleBackwardBlocks(List<Instruction> list){
		HashSet<Integer> begun = new HashSet<Integer>();
		HashSet<Integer> ended = new HashSet<Integer>();
		
		int blockEnd = -1;
		
		for(int i = list.size() - 1; i >= 0; i--){
			Instruction o = list.get(i);
			if(isCorrectJump(o, false)){
				if(blockEnd == -1)blockEnd = i;
				begun.add(((Jump) o).target);
				
			}
			if(begun.contains(o.location)){
				ended.add(o.location);
			}
			if(ended.size() == begun.size() && blockEnd != -1){
				if(i == 0 || list.get(i).location != list.get(i-1).location){
					Block block = Block.createBlock(list, i, blockEnd+1, o.location, false);
					setJumpBlock(list, o.location, block, false);
					applySimpleBackwardBlocks(block.body);
					applySimpleBackwardBlocks(list.subList(0, i));
					return;
				}
			}
		}
	}
	
	private static boolean isCorrectJump(Instruction o, boolean forward){
		return o instanceof Jump && ((Jump)o).isForward() == forward && ((Jump)o).getBlock() == null;
	}
}
