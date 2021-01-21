package org.golde.discordbot.website;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.website.event.EventStatsRoute;
import org.golde.discordbot.website.server.ESSWebServer;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class WebsiteBot extends ESSBot {

	private static WebsiteBot instance;
	
	public static void main(String[] args) throws Exception {
		instance = new WebsiteBot();
		instance.run();
	}
	
	public static WebsiteBot getInstance() {
		return instance;
	}
	
	@Override
	public String getPrefix() {
		return null;
	}

	@Override
	public void onReady() {
		ESSWebServer.startWebServer();
		getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("for http requests"));
	}
	
	@Override
	public void registerEventListeners(List<EventBase> events) {
		events.add(new EventStatsRoute(this));
		super.registerEventListeners(events);
	}

}
