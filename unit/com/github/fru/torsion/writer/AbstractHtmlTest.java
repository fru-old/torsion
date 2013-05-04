package com.github.fru.torsion.writer;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractHtmlTest {

	public PrintWriter body = null;
	public PrintWriter js = null;
	private File temp = null;
	
	@Before
	public void createHtml() throws FileNotFoundException, IOException{
		File tempJs;
		body = new PrintWriter(temp = createTempFile("index.html"));
		js = new PrintWriter(tempJs = createTempFile("script.js"));
		body.append("<html>");
		body.append("<head>");
		body.append("<script type=\"text/javascript\" src=\""+tempJs.toURI()+"\"></script>");
		body.append("</head>");
		body.append("<body>");
	}
	
	@After
	public void displayFile() throws IOException{
		body.append("</body>");
		body.append("</html>");
		body.close();
		js.close();
		if(Desktop.isDesktopSupported()){
		  Desktop.getDesktop().browse(temp.toURI());
		}
	}
	
	public static File createTempFile(String name) throws IOException {
		File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
		if (!temp.delete()) throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		if (!temp.mkdir()) throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		return new  File(temp, name);
	}
}
