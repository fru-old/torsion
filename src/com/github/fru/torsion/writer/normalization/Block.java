package com.github.fru.torsion.writer.normalization;

import java.util.List;

import com.github.fru.torsion.writer.container.FinalList;
import com.github.fru.torsion.writer.container.Union3;

public class Block extends Operation{

	public static enum Direction{
		FORWARD, BACKWARD;
	}
	
	private Direction direction;
	public final FinalList<Operation> body;
	
	public Block(Direction direction){
		super(direction == Direction.FORWARD ? Operation.Type.BLOCK_FORWARD : Operation.Type.BLOCK_BACKWARD );
		this.direction = direction;
		this.body = new FinalList<Operation>();
	}
	
	public Direction getDirection(){
		return direction;
	}
	
	public int getId(){
		return super.hashCode();
	}
	
	public String toString(){
		return Method.BlockToString(""+getId(), null, body);
	}
	
	//Normalization
	
	private static FinalList<Operation> applyBlock(FinalList<Operation> original, int start, int end, Direction direction){
		FinalList<Operation> out = new FinalList<Operation>();
		Block block = new Block(direction);
		block.body.addAll(original.subList(start, end));
		out.addAll(original.subList(0, start));
		out.add(block);
		out.addAll(original.subList(end, original.size()));
		return out;
	}
}
