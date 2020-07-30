package org.golde.discordbot.shared.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtil {

	public static List<String> readGenericConfig(String file) {
		return readGenericConfig(file, true);
	}
	
	public static List<String> readGenericConfig(String file, boolean res) {
		
		List<String> temp = new ArrayList<String>();
		
		try (Scanner scanner = new Scanner(new File((res ? "res/" : "") + file + ".config"))) {

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
