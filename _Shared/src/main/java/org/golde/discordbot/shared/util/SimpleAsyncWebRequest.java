package org.golde.discordbot.shared.util;

import java.io.IOException;

import org.golde.discordbot.shared.util.SimpleSyncWebRequest.ResponseWithCode;

public class SimpleAsyncWebRequest {
	private final WebRequestCallback callback;
	
	private final SimpleSyncWebRequest syncRequest;

	public SimpleAsyncWebRequest(String endpoint) {
		this(endpoint, null);
	}
	
	public SimpleAsyncWebRequest(String endpoint, WebRequestCallback callback) {
		this.syncRequest = new SimpleSyncWebRequest(endpoint);
		this.callback = callback;
	}

	public SimpleAsyncWebRequest addParam(String key, String value) {
		syncRequest.addParam(key, value);
		return this;
	}

	public void send() {

		new Thread() {

			public void start() {

				try {
					ResponseWithCode response = syncRequest.start();
					if(callback != null) {
						callback.onResponse(response.getStatusCode(), response.getContent());
					}
					
				}
				catch(IOException e) {
					if(callback != null) {
						callback.onIOError(e);
					}
					else {
						e.printStackTrace();
					}
				}

			}

		}.start();

	}
	
	public interface WebRequestCallback {

		public void onResponse(int statusCode, String response);
		public void onIOError(IOException ex);
		
	}

}
