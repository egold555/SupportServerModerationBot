package org.golde.discordbot.website.express.routes;

import org.golde.discordbot.website.WebsiteBot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import express.http.request.Request;
import express.http.response.Response;
import express.utils.MediaType;
import express.utils.Status;
import net.dv8tion.jda.api.entities.Guild;

public abstract class Route {

	public abstract void onRequest(Request req, Response res);
	
	protected static final String KEY_ERROR_MESSAGE = "message";
	protected static final String KEY_ERROR_CODE = "statusCode";
	
	private static final Gson GSON = new GsonBuilder().serializeNulls().create();
	private static final Gson GSON_PRETTY = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	
	protected static void sendSuccess(Request req, Response res, JsonObject data) {
		JsonObject root = new JsonObject();

		root.addProperty("success", true);
		root.add("data", data);

		send(req, res, root);
	}

	protected static void sendError(Request req, Response res, int errorCode, String error) {
		JsonObject errorObj = new JsonObject();
		errorObj.addProperty(KEY_ERROR_MESSAGE, error);
		sendError(req, res, errorCode, errorObj);
	}

	protected static void sendError(Request req, Response res, int errorCode, JsonObject error) {
		error.addProperty(KEY_ERROR_CODE, errorCode);

		JsonObject root = new JsonObject();

		root.addProperty("success", false);
		root.add("error", error);
		
		res.setStatus(Status.valueOf(errorCode));
		send(req, res, root);
	}
	
	private static void send(Request req, Response res, JsonObject root) {
		
		Boolean pretty = getBooleanFromQuery(req, "pretty");
		if(pretty == null) {
			pretty = false;
		}
		
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setContentType(MediaType._json);
		res.send(pretty ? GSON_PRETTY.toJson(root) : GSON.toJson(root));
	}
	
	protected static final Guild getGuild() {
		return WebsiteBot.getInstance().getGuild();
	}
	
	protected static final Boolean getBooleanFromQuery(Request req, String in) {
		final String val = req.getFormQuery(in);
		
		if(val == null) {
			return null;
		}
		return Boolean.parseBoolean(val);
	}
	
	protected static final Integer getIntegerFromQuery(Request req, String in) {
		final String val = req.getFormQuery(in);
		if(val == null) {
			return null;
		}
		try {
			return Integer.parseInt(val);
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
	
}
