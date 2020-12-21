package org.golde.discordbot.website.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.website.server.routes.Page404;
import org.golde.discordbot.website.server.routes.PageIndex;
import org.golde.discordbot.website.server.routes.pages.PageAllMembers;
import org.golde.discordbot.website.server.routes.stats.PageGeneralStats;

import fi.iki.elonen.router.RouterNanoHTTPD;

public class ESSWebServer extends RouterNanoHTTPD {
	
	public static long start;
	public static final List<String> routes = new ArrayList<String>();
	
	public static void startWebServer() {
		new Thread() {
			public void run() {
				try {
					ESSWebServer server = new ESSWebServer(8888);
					server.setNotFoundHandler(Page404.class);
					server.start(ESSWebServer.SOCKET_READ_TIMEOUT, false);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				
			};
		}.start();
	}
	
	private ESSWebServer(int port) {
		super(port);
		addMappings();
		start = System.currentTimeMillis();
	}

	@Override
	public void addMappings() {
		super.addMappings();
		
		addRoute("/", PageIndex.class);
		addRoute("/stats", PageGeneralStats.class);
		addRoute("/members", PageAllMembers.class);
	}
	
	@Override
	public void addRoute(String url, Class<?> handler, Object... initParameter) {
		routes.add(url);
		super.addRoute(url, handler, initParameter);
	}

}
