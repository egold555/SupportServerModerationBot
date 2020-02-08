package org.golde.discordbot.supportserver.database;

import java.util.ArrayList;
import java.util.List;

public class Database {

	private static final List<SimpleUser> USERS = new ArrayList<SimpleUser>();
	
	public static void saveToFile() {
		
	}
	
	public static void loadFromFile() {
		
	}
	
	public static List<SimpleUser> getAllUsers() {
		return USERS;
	}
	
	public static SimpleUser getUser(long snowflake) {
		
		for(SimpleUser u : USERS) {
			if(u.getSnowflake() == snowflake) {
				return u;
			}
		}
		
		SimpleUser u = new SimpleUser(snowflake);
		USERS.add(u);
		return u;
		
	}
	
}
