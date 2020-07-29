package org.golde.discordbot.supportserver.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FileUtil {

	public static Set<String> readGenericConfig(String file) {
		
		Set<String> temp = new HashSet<String>();
		
		try (Scanner scanner = new Scanner(new File("res/" + file + ".txt"))) {

			while (scanner.hasNext()){
				String next = scanner.nextLine();
				if(!next.isEmpty() && !next.startsWith("#")) {
					temp.add(next);
				}
			}

		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return temp;
		
	}
	
}
