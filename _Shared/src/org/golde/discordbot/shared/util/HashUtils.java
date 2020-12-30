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

		return md5(complete.digest());
	}
	
	public static final String md5(String data) {
		return md5(data.getBytes());
	}

	public static final String md5(byte[] data) {
		String result = "";

		for (int i=0; i < data.length; i++) {
			result += Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result.toUpperCase();
	}

}
