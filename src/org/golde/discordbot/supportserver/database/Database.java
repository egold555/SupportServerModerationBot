package org.golde.discordbot.supportserver.database;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Database {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private static List<SimpleUser> USERS = new ArrayList<SimpleUser>();
	
	public static final String USERNAME_CACHE_FILE = "userdata-cache";
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
		USERS = loadFromFile(USERS_FILE, SimpleUser[].class);
	}
	
	public static List<SimpleUser> getAllUsers() {
		return USERS;
	}
	
	public static SimpleUser getUser(long snowflake) {
		
		for(SimpleUser u : USERS) {
			if(u.getUser().getSnowflake() == snowflake) {
				return u;
			}
		}

		SimpleUser u = new SimpleUser(new UserDataCache(snowflake));
		USERS.add(u);
		return u;
		
	}
	
	public static void addOffence(long snowflake, long moderator, ModAction action, String reason) {
		getUser(snowflake).addOffence(new Offence(action, new UserDataCache(moderator), reason, System.currentTimeMillis()));
		saveToFile(USERS, USERS_FILE);
	}
	
	public static void removeOffence(long snowflake, ModAction action) {
		getUser(snowflake).removeLastOffence(action);
		saveToFile(USERS, USERS_FILE);
	}
	
}
