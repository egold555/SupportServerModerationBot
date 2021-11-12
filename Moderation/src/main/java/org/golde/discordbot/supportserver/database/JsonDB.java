package org.golde.discordbot.supportserver.database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonDB {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public static void saveToFile(Object list, String filename) {

		FileWriter writer = null;
		
		try {
			writer = new FileWriter("res/" + filename + ".json");
			GSON.toJson(list, writer);
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
	
	public static <T> List<T> loadFromFile(String filename, Class<T[]> clazz) {
		
		FileReader reader = null;
		
		try {
			reader = new FileReader("res/" + filename + ".json");
			T[] arr = new Gson().fromJson(reader, clazz);
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
	
	public static <T> T jsonFile2JavaObject(File file, Class<T> clazz) {

		FileReader reader = null;

		try {
			reader = new FileReader(file);
			T arr = new Gson().fromJson(reader, clazz);
			return arr;
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

	public static <T> T jsonFile2JavaObject(String file, Class<T> clazz) {

		return jsonFile2JavaObject(new File("res/" + file + ".json"), clazz);

	}
	
}
