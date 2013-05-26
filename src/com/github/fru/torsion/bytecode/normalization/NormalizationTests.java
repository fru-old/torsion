package com.github.fru.torsion.bytecode.normalization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class NormalizationTests {

	
	public void print(Iterable<Instruction> body){
		for(Instruction o : body){
			System.out.println(o);
		}
	}
	
	public ArrayList<Instruction> init(int jumps, long seed){
		Random r = new Random(seed);
		ArrayList<Instruction> out = new ArrayList<Instruction>();
		for(int i = 0; i < jumps; i++){
			int target = r.nextInt(jumps);
			out.add(new Instruction.Jump(i, target));
		}
		return out;
	}
	
	@Test
	public void printTest(){
		long seed = System.currentTimeMillis();//1368625650230L;//1368624666118L;
		System.out.println(seed);
		List<Instruction> list = init( 2, seed); 
		Block.applyBlocks(list);
		print(list);
	}
	
}
