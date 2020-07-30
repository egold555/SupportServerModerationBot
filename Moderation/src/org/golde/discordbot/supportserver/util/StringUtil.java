package org.golde.discordbot.supportserver.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


public class StringUtil {

	public static String toStringException(Throwable t) throws IOException {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String toReturn = sw.toString();
		pw.close();
		sw.close();
		return toReturn;
	}

}
