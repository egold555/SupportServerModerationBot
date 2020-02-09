package org.golde.discordbot.supportserver.database;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import lombok.Getter;


public class UserDataCache {

	@Getter private final long snowflake;
	private String username = "null";
	private String avatar = "null";
	
	public UserDataCache(long snowflake) {
		this.snowflake = snowflake;
	}
	
	public void setUsername(String username) {
		try {
			this.username = URLEncoder.encode(username, "UTF-8").replace("%23", "#").replace("+", " ");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getUsername() {
		try {
			return URLDecoder.decode(username, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return username;
		}
	}

	@Override
	public String toString() {
		return "UserDataCache [snowflake=" + snowflake + ", username=" + username + ", avatar=" + avatar + "]";
	}
	
	
}
