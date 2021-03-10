package org.golde.discordbot.website.express.routes;

import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.JsonObject;

import express.http.request.Request;
import express.http.response.Response;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class RouteCached extends Route {

	private static Timer timer = new Timer();
	@Getter(value = AccessLevel.PROTECTED)
	private JsonObject cacheData = null;
	private JsonObject cacheError = null;

	public RouteCached() {
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				System.out.println("Updating...");
				JsonObject dataTmp = new JsonObject();
				JsonObject errorTmp = new JsonObject();
				updateCache(dataTmp, errorTmp);
				if(errorTmp.has(KEY_ERROR_MESSAGE) || errorTmp.has(KEY_ERROR_CODE)) {
					cacheData = null;
				}
				else {
					cacheData = dataTmp;
				}
				
				cacheError = errorTmp;
			}
		}, 1000, getTimeUntilCacheExpiresInMS());
	}

	protected int getTimeUntilCacheExpiresInMS() {
		return 5000;
	}

	protected abstract void updateCache(JsonObject data, JsonObject error);

	protected final void sendCachedDataReply(Request req, Response res) {
		if(cacheData != null) {
			
			sendSuccess(req, res, cacheData);
		}
		else {
			if(cacheError != null) {
				if(cacheError.has(KEY_ERROR_MESSAGE) && cacheError.has(KEY_ERROR_CODE)) {
					sendError(req, res, cacheError.get(KEY_ERROR_CODE).getAsInt(), cacheError.get(KEY_ERROR_MESSAGE).getAsString());
					
				}
				else if(cacheError.has(KEY_ERROR_MESSAGE) && !cacheError.has(KEY_ERROR_CODE)){
					sendError(req, res, 500, cacheError.get(KEY_ERROR_MESSAGE).getAsString());
				}
				else {
					sendError(req, res, cacheError.get(KEY_ERROR_CODE).getAsInt(), "No error message specified.");
				}
			}
			else {
				sendError(req, res, 500, "No error message specified. (You should never ever see this message)");
			}
		}
	}

}
