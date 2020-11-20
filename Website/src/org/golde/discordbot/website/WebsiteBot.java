package org.golde.discordbot.website;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.website.command.owner.CommandTest;
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
		return "=";
	}

	@Override
	public void onReady() {
		ESSWebServer.startWebServer();
		getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("for http requests"));
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void onReload() {

	}

	@Override
	public void registerEventListeners(List<EventBase> events) {
		
	}

	@Override
	public void registerEveryoneCommand(List<EveryoneCommand> cmds) {
		
	}

	@Override
	public void registerChatModCommand(List<ChatModCommand> cmds) {
		
	}

	@Override
	public void registerGuildModCommand(List<GuildModCommand> cmds) {

	}

	@Override
	public void registerOwnerCommand(List<OwnerCommand> cmds) {
		cmds.add(new CommandTest(this));
	}

}
