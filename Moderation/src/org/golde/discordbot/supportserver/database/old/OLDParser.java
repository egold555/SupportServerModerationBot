package org.golde.discordbot.supportserver.database.old;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.database.old.parse.OldOffence;
import org.golde.discordbot.supportserver.database.old.parse.SimpleUser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OLDParser {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static List<SimpleUser> USERS = new ArrayList<SimpleUser>();

	public static final String USERNAME_CACHE_FILE = "userdata-cache";
	public static final String USERS_FILE = "user-data";

	public static void go(ESSBot bot) {
		USERS = Database.loadFromFile(USERS_FILE, SimpleUser[].class);
		
		for(SimpleUser su : USERS) {
			System.out.println("User: " + su.getUser().getSnowflake());
			for(OldOffence oo : su.getOffences()) {
				System.out.println("	- " + oo.toString());
				Offence.addOffence(bot, oo.translate(su.getUser()));
			}
			
			
		}
	}

}
