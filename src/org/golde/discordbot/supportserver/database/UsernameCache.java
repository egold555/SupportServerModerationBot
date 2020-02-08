package org.golde.discordbot.supportserver.database;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UsernameCache {

	private final long snowflake;
	@Setter private String username = "null";
	
	public UsernameCache(long snowflake) {
		this.snowflake = snowflake;
	}
	
	
	
}
