package org.golde.discordbot.supportserver.database;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Database {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private static List<SimpleUser> USERS = new ArrayList<SimpleUser>();
	private static List<UsernameCache> USERNAME_CACHE = new ArrayList<UsernameCache>();
	
	public static final String USERNAME_CACHE_FILE = "username-cache";
	public static final String USERS_FILE = "user-data";
	
	public static void saveToFile(List<?> list, String filename) {

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
	
	public static void loadAllFromFile() {
		USERNAME_CACHE = loadFromFile(USERNAME_CACHE_FILE, UsernameCache[].class);
		USERS = loadFromFile(USERS_FILE, SimpleUser[].class);
	}
	
	public static List<SimpleUser> getAllUsers() {
		return USERS;
	}
	
	public static List<UsernameCache> getUsernameCashe() {
		return USERNAME_CACHE;
	}
	
	public static SimpleUser getUser(long snowflake) {
		
		for(SimpleUser u : USERS) {
			if(u.getUser().getSnowflake() == snowflake) {
				return u;
			}
		}
		
		System.out.println("creating new SimpleUser " + snowflake);
		SimpleUser u = new SimpleUser(getUsernameCache(snowflake));
		USERS.add(u);
		return u;
		
	}
	
	public static UsernameCache getUsernameCache(long snowflake) {
		
		for(UsernameCache c : USERNAME_CACHE) {
			if(c.getSnowflake() == snowflake) {
				return c;
			}
		}
		
		UsernameCache c = new UsernameCache(snowflake);
		USERNAME_CACHE.add(c);
		System.out.println("creating new UsernameCache " + snowflake);
		saveToFile(USERNAME_CACHE, USERNAME_CACHE_FILE);
		return c;
		
	}
	
	public static void updateUsername(long snowflake, String name) {
		getUsernameCache(snowflake).setUsername(name);
		saveToFile(USERNAME_CACHE, USERNAME_CACHE_FILE);
		System.out.println("Updating username " + snowflake + " - " + name);
	}
	
}
