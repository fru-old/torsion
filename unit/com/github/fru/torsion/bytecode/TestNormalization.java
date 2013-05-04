package com.github.fru.torsion.bytecode;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.github.fru.torsion.bytecode.utils.CodeList;
import com.github.fru.torsion.bytecode.utils.Instruction;

public class TestNormalization {

	Random random = new Random();
	CodeList<Instruction> randomJumps;
	
	@Before
	public void initRandomJumps(){
		randomJumps = new CodeList<Instruction>();
		int size = 5;
		
		for(int i = 0; i < size; i++){
			//randomJumps.add(new Instruction( ":", i, null));
			//randomJumps.add(new Instruction("goto",null,random.nextInt(size)));
		}
		
		System.out.println(randomJumps.toString());
	}
	
	
	@Test
	public void executeStepOne(){
		BytecodeNormalization.normalize(randomJumps);
		System.out.println(randomJumps.toString());
	}
}
