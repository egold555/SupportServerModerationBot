package org.golde.discordbot.shared.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HashUtils {

	public static final String md5(InputStream is) throws NoSuchAlgorithmException, IOException {
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = is.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		is.close();

		byte[] result = complete.digest();

		String textResult = "";
		
		for (int i=0; i < result.length; i++) {
			textResult += Integer.toString( ( result[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return textResult.toUpperCase();
	}
	
	public static String md5(String in) {
		
		try {
			MessageDigest complete = MessageDigest.getInstance("MD5");
			
			complete.update(in.getBytes());
			
			byte[] result = complete.digest();

			String textResult = "";
			
			for (int i=0; i < result.length; i++) {
				textResult += Integer.toString( ( result[i] & 0xff ) + 0x100, 16).substring( 1 );
			}
			
			return textResult;
		}
		catch(NoSuchAlgorithmException neverThrown) {
			neverThrown.printStackTrace();
			return "";
		}
		
	}
	
}
