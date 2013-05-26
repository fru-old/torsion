package com.github.fru.torsion.writer;

import org.junit.Test;

import com.github.fru.torsion.writer.normalization.Structure;

public class JSBasicTest extends AbstractHtmlTest{

	@Test
	public void basic(){
		JavascriptWriter writer = new JavascriptWriter(js, "basic");
		writer.addType(new Structure("ba",null));
		js.append("alert(basic.ba);");
		writer.close();
	}
}
