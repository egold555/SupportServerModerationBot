package org.golde.discordbot.shared.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtil {

	private StringUtil() {};
	
	public static final String toString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String result = sw.toString();
		pw.close();
		try {
			sw.close();
		} 
		catch (IOException e1) {
			
		}
		return result;
	}
	
}
