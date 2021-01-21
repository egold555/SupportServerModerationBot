package org.golde.discordbot.shared.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VersionUtil {
	
	public static String getHash() {
		
		InputStream in = VersionUtil.class.getResourceAsStream("/git-hash.txt"); 
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally { 
			try {
				reader.close();
			} catch (IOException e) {}
		}
		return "ERROR-NO-HASH";
	}
	
	public static String getHashShort() {
		String hashIn = getHash();
		if(hashIn == null) {
			return null;
		}
		return hashIn.substring(0, 7);
	}
}
