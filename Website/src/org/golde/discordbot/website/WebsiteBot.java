package org.golde.discordbot.website;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.db.AbstractDBTranslation;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.website.db.Offence;
import org.golde.discordbot.website.express.ESSWebServerExpress;

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
	public void registerDatabaseTranslations(List<Class<? extends AbstractDBTranslation>> dbt) {
		dbt.add(Offence.class);
		super.registerDatabaseTranslations(dbt);
	}

	@Override
	public void onReady() {
		ESSWebServerExpress.startWebServer();
		getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("for http requests"));
	}
	
	@Override
	public void registerEventListeners(List<EventBase> events) {
		super.registerEventListeners(events);
	}

}
