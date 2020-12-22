package org.golde.discordbot.utilities.db;

import org.golde.discordbot.shared.db.FileUtil;

import lombok.Getter;

public class DB {

	private static DB INSTANCE;
	
	@Getter private APIKeys apiKeys;
	
	public static DB getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DB();
		}
		return INSTANCE;
	}
	
	public void load() {
		
		apiKeys = FileUtil.loadFromFile("api-keys", APIKeys.class);
		
	}

	public void reload() {
		// TODO Auto-generated method stub
		
	}
	
}
