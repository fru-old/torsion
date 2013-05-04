package com.github.fru.torsion.writer;

import org.junit.Test;

public class JSBasicTest extends AbstractHtmlTest{

	@Test
	public void basic(){
		body.append("test..");
		js.append("alert('test')");
	}
}
