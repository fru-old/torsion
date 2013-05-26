package com.github.fru.torsion.writer.normalization;

import java.util.Random;

import org.junit.Test;

import com.github.fru.torsion.writer.normalization.Block.Direction;

public class BlocktTest {

	@Test
	public void printSimpleGotos(){
		Method m = new Method(new Identifier("test"));
		int count = 10;
		Random random = new Random();
		for(int i = 0; i < count; i++){
			m.body.add(new PrimitiveOperation(PrimitiveOperation.Type.NOOP,i));
			int target = random.nextInt(count-1);
			if(target >= i)target++;
			PrimitiveOperation.Type t = target > i ? PrimitiveOperation.Type.GOTO_FORWARD : PrimitiveOperation.Type.GOTO_BACKWARD;
			m.body.add(new PrimitiveOperation(t,i).setJump(target));
		}
		m.body.setImmutable();
		PrimitiveOperation.assignJumpOperation(m);
		//m = new Method(m, Block.applyBlock(m.body, 2, 15, Direction.FORWARD));
		System.out.println(m.toString());
	}
	
}
