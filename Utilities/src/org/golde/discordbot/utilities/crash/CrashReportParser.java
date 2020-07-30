package org.golde.discordbot.utilities.crash;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

import org.golde.discordbot.utilities.crash.CrashReport.Stacktrace;

public class CrashReportParser {

	public static CrashReport parse(File f) throws FileNotFoundException, ParseException {
		
		Scanner s = new Scanner(f);
		CrashReport.CrashReportBuilder b = CrashReport.builder();
		
		
		String in = s.nextLine(); //---- Minecraft Crash Report ----
		
		if(!in.equals("---- Minecraft Crash Report ----")) {
			s.close();
			return null;
		}
		
		b.wittyComment(remove(s.nextLine(), "// "));
		s.nextLine(); //Blank Line
		
		b.time(remove(s.nextLine(), "Time: "));
		b.description(remove(s.nextLine(), "Description: "));
		
		s.nextLine(); //Blank Line
		
		//exception
		StringBuilder sb = new StringBuilder();
		while(s.hasNextLine()) {
			String line = s.nextLine();
			if(line.startsWith("A detailed walkthrough of the error, ")){
				break;
			}
			sb.append(line + "\n");
		}
		
		b.exception(extractException(sb));
		
		//remove everything up to the head stacktrace:
		for(int i = 0; i < 4; i++) {
			s.nextLine();
		}
		
		sb = new StringBuilder();
		//head stacktrace
		while(s.hasNextLine()) {
			String line = s.nextLine();
			if(line.isEmpty()) {
				break;
			}
			
			sb.append(remove(line, "	at ") + "\n");
		}
		
		b.head(sb.toString().split("\n"));
		
		
		//remove everything up to the Initialization  stacktrace:
		for(int i = 0; i < 3; i++) {
			s.nextLine();
		}
		
		//Initialization  stacktrace
		while(s.hasNextLine()) {
			String line = s.nextLine();
			if(line.isEmpty()) {
				break;
			}
			
			sb.append(remove(line, "	at ") + "\n");
		}
		
		b.initialization(sb.toString().split("\n"));
		
		
		
		s.close();
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
