package org.golde.discordbot.supportserver.database;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import lombok.Getter;


public class UsernameCache {

	@Getter private final long snowflake;
	private String username = "null";
	
	public UsernameCache(long snowflake) {
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
	
	public String getUsername() {
		try {
			return URLDecoder.decode(username, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return username;
		}
	}
	
}
