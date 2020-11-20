package org.golde.discordbot.website.server.routes.base;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public abstract class AbstractJsonResponse extends DefaultHandler {

	protected static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private String errorMessage = "";
	private IStatus status = Status.OK;
	
	@Override
    public final String getText() {
        return "idfk what this does but I need to implement it and it doesn't seem to be used";
    }

    public abstract JsonObject getResponse(Map<String, String> urlParams, IHTTPSession session, JsonObject root);
    
    protected String getErrorMessage() {
    	return errorMessage;
    }
    
    public void setErrored() {
		this.setErrored("No error message specified.");
	}
    
    public void setErrored(String errorMessage) {
		this.setErrored(errorMessage, Status.BAD_REQUEST);
	}

    public void setErrored(String errorMessage, Status status) {
		this.errorMessage = errorMessage;
		this.status = status;
	}
    
    @Override
    public final String getMimeType() {
        return "application/json";
    }

    @Override
    public IStatus getStatus() {
        return status;
    }

    public final Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
    	
    	JsonObject responce = getResponse(urlParams, session, new JsonObject());
    	
    	JsonObject obj = new JsonObject();
    	
    	boolean success = getStatus().equals(Status.OK);
    	
    	obj.addProperty("success", success);
    	if(success) {
    		obj.add("data", responce);
    	}
    	else {
    		JsonObject error = new JsonObject();
    		error.addProperty("message", getErrorMessage());
    		error.addProperty("status", getStatus().getDescription());
    		obj.add("error", error);
    	}
        String text = gson.toJson(obj);
        ByteArrayInputStream inp;
		//utf8 fix
        try {
			inp = new ByteArrayInputStream(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			inp = new ByteArrayInputStream(text.getBytes());
			e.printStackTrace();
		}
        int size = text.getBytes().length;
        Response response = NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, size);
        
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }
	
}
