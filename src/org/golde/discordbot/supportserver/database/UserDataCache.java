package org.golde.discordbot.supportserver.database;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import lombok.Getter;

@Deprecated
public class UserDataCache {

	@Getter private final long snowflake;
	private String username = "null"; //unused
	private String avatar = "null"; //unused
	
	public UserDataCache(long snowflake) {
		this.snowflake = snowflake;
	}

	@Override
	public String toString() {
		return "UserDataCache [snowflake=" + snowflake + "]";
	}
	
	
}
