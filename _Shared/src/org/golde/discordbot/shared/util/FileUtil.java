package org.golde.discordbot.shared.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.golde.discordbot.shared.ESSBot;

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

	public static void saveToFile(Object list, String filename) {

		FileWriter writer = null;

		try {
			writer = new FileWriter("res/" + filename + ".json");
			ESSBot.GSON.toJson(list, writer);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(writer != null) {
				try {
					writer.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static <T> List<T> loadArrayFromFile(String filename, Class<T[]> clazz) {

		FileReader reader = null;

		try {
			reader = new FileReader("res/" + filename + ".json");
			T[] arr = ESSBot.GSON.fromJson(reader, clazz);
			return new ArrayList<>(Arrays.asList(arr)); //Arrays.asList returns a fixed size list. Jolly. https://stackoverflow.com/questions/5755477/java-list-add-unsupportedoperationexception
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}

		}


		return null;

	}

	public static <T> T loadFromFile(String filename, Class<T> clazz) {

		FileReader reader = null;

		try {
			reader = new FileReader("res/" + filename + ".json");
			return ESSBot.GSON.fromJson(reader, clazz);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}

		}


		return null;

	}

}
