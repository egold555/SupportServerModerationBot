package org.golde.discordbot.website.server;

import java.io.IOException;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.website.server.routes.PageIndex;
import org.golde.discordbot.website.server.routes.ResponseGeneralStats;
import org.golde.discordbot.website.server.routes.ResponseHolidayImages;

import fi.iki.elonen.router.RouterNanoHTTPD;

public class ESSWebServer extends RouterNanoHTTPD {
	
	public static void startWebServer() {
		new Thread() {
			public void run() {
				try {
					ESSWebServer server = new ESSWebServer(8894);
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
	}

	@Override
	public void addMappings() {
		super.addMappings();
		addRoute("/", PageIndex.class);
		addRoute("/stats", ResponseGeneralStats.class);
		addRoute("/holiday", ResponseHolidayImages.class);
	}

}
