package org.golde.discordbot.utilities.crash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.golde.discordbot.utilities.crash.CrashReport.Stacktrace;

public class CrashReportParser {

	public static CrashReport parse(File f) throws ParseException, IOException {

		List<String> linesOrig = Files.readAllLines(f.toPath(), Charset.forName("ISO-8859-1"));
		
		Queue<String> lines = new LinkedList<String>(linesOrig);

		CrashReport.CrashReportBuilder b = CrashReport.builder();


		String in = lines.poll(); //---- Minecraft Crash Report ----

		if(!in.equals("---- Minecraft Crash Report ----")) {
			return null;
		}

		b.wittyComment(remove(lines.poll(), "// "));
		lines.poll(); //Blank Line

		b.time(remove(lines.poll(), "Time: "));
		b.description(remove(lines.poll(), "Description: "));

		lines.poll(); //Blank Line

		//exception
		StringBuilder sb = new StringBuilder();
		while(true) {
			String line = lines.poll();
			if(line == null || line.startsWith("A detailed walkthrough of the error, ")){
				break;
			}
			if(line != null) {
				sb.append(line + "\n");
			}
			
		}

		b.exception(extractException(sb));

		//remove everything up to the head stacktrace:
		for(int i = 0; i < 4; i++) {
			lines.poll();
		}

		sb = new StringBuilder();
		//head stacktrace
		while(true) {
			String line = lines.poll();
			if(line == null || line.isEmpty()) {
				break;
			}

			if(line != null) {
				sb.append(remove(line, "	at ") + "\n");
			}
			
			
		}

		b.head(sb.toString().split("\n"));


		//remove everything up to the Initialization  stacktrace:
		for(int i = 0; i < 3; i++) {
			lines.poll();
		}

		//Initialization  stacktrace
		while(true) {
			String line = lines.poll();
			if(line == null || line.isEmpty()) {
				break;
			}

			if(line != null) {
				sb.append(remove(line, "	at ") + "\n");
			}
			
		}

		b.initialization(sb.toString().split("\n"));



		//s.close();
		return b.build();

	}

	private static String remove(String in, String... toRemove) {
		for(String r : toRemove) {
			in = in.replace(r, "");
		}
		return in;
	}

	private static Stacktrace extractException(StringBuilder sb) {

		String toString = sb.toString();
		String[] firstArray = toString.split("\n");
		String[] stacktrace = new String[firstArray.length - 1];

		for(int i = 0; i < stacktrace.length; i++) {
			String s = firstArray[i + 1];
			stacktrace[i] = remove(s, "	at ");
		}

		String[] firstLine = firstArray[0].split(":");

		if(firstLine.length != 2) {
			//sometimes this is the case?
			return new Stacktrace(firstLine[0], "", stacktrace);
		}
		return new Stacktrace(firstLine[0], firstLine[1].substring(1), stacktrace);

	}

}
