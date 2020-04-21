package org.golde.discordbot.supportserver.crash;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.golde.discordbot.supportserver.crash.CrashReport.IntCache;
import org.golde.discordbot.supportserver.crash.CrashReport.Stacktrace;
import org.golde.discordbot.supportserver.crash.CrashReport.SystemDetails;
import org.golde.discordbot.supportserver.crash.CrashReport.SystemDetails.EnumModded;
import org.golde.discordbot.supportserver.crash.CrashReport.SystemDetails.SystemDetailsBuilder;



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
		
		//remove everything in prep for system details
		for(int i = 0; i < 2; i++) {
			s.nextLine();
		}
		
		SystemDetailsBuilder sdb = SystemDetails.builder();
		
		//sdb.minecraftVersion(remove(s.nextLine(), ""));
		sdb.minecraftVersion(remove(s.nextLine(), "	Minecraft Version: "));
		sdb.operatingSystem(remove(s.nextLine(), "	Operating System: "));
		sdb.CPU(remove(s.nextLine(), "	CPU: "));
		sdb.javaVersion(remove(s.nextLine(), "	Java Version: "));
		sdb.javaVMVersion(remove(s.nextLine(), "	Java VM Version: "));
		sdb.memory(remove(s.nextLine(), "	Memory: "));
		sdb.jVMFlags(parseJVMFlags(s.nextLine()));
		
		Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s.nextLine());
//        while(m.find()) {
//            System.out.println(m.group());
//        }
        m.find();
        int f1 = Integer.parseInt(m.group());
        m.find();
        int f2 = Integer.parseInt(m.group());
        m.find();
        int f3 = Integer.parseInt(m.group());
        m.find();
        int f4 = Integer.parseInt(m.group());
        

		sdb.intCache(new IntCache(f1,f2, f3, f4));
		
		sdb.launchedVersion(remove(s.nextLine(), "	"));
		sdb.LWJGL(s.nextLine());
		sdb.OpenGL(s.nextLine());
		
		sb = new StringBuilder();
		
		String glCaps = s.nextLine();
		glCaps = remove(glCaps, "	GL Caps: ");
		
		if(glCaps.length() > 0) {
			sb.append(glCaps + "\n");
			while(s.hasNextLine()) {
				String line = s.nextLine();
				if(line.isEmpty()) {
					break;
				}
				line = remove(line, "	GL Caps: ");

				
				sb.append(line + "\n");
			}
		}
		else {
			sb.append("");
		}
		
		
		//TODO: BUG - Random space being added when splitting
		sdb.GLCaps(sb.toString().split("\n"));
		//System.out.println(Arrays.toString(sb.toString().split("\n")));
		
		sdb.usingVBOs(remove(s.nextLine(), "	Using VBOs: ").equals("Yes"));
		sdb.isModded(EnumModded.get(remove(s.nextLine(), "	Is Modded: ").split(";")[0].replace(",", "")));
		sdb.type(remove(s.nextLine(), "	Type: "));
		sdb.resourcePacks(remove(s.nextLine(), "	Resource Packs: ").split(","));
		sdb.currentLanguage(remove(s.nextLine(), "	Current Language: "));
		sdb.profilerPosition(remove(s.nextLine(), "	Profiler Position: "));
		b.systemDetails(sdb.build());
		
		s.close();
		return b.build();
		
	}
	
	private static String[] parseJVMFlags(String line) {
		String[] firstArray = remove(line, "	JVM Flags: ").split(" ");
		String[] toReturn = new String[firstArray.length - 2];
		
		for(int i = 0; i < toReturn.length; i++) {
			toReturn[i] = firstArray[i + 2];
		}
		
		return toReturn;
		
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
