package org.golde.discordbot.shared.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SimpleSyncWebRequest {

	private final String endpoint;
	private Map<String, String> params = new HashMap<String, String>();

	public SimpleSyncWebRequest(String endpoint) {
		this.endpoint = endpoint;
	}

	public SimpleSyncWebRequest addParam(String key, String value) {
		params.put(key, value);
		return this;
	}

	public ResponseWithCode start() throws IOException {


		URL url = new URL(endpoint);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		con.setInstanceFollowRedirects(false);
		
		String urlParamsString = getParamsString(params);

		if(params.size() > 0) {
			con.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			out.writeBytes(urlParamsString);
			out.flush();
			out.close();
		}

		//System.out.println("Send request: " + endpoint + ((params.size() > 0) ? " with params: " + urlParamsString : ""));
		
		con.connect();

		int status = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		con.disconnect();
		
		return new ResponseWithCode(status, content.toString());

	}

	private static String getParamsString(Map<String, String> params)  throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}

		String resultString = result.toString();
		return resultString.length() > 0
				? resultString.substring(0, resultString.length() - 1)
						: resultString;
	}
	
	@AllArgsConstructor
	@Getter
	public static class ResponseWithCode {
		private int statusCode;
		private String content;
	}

}
