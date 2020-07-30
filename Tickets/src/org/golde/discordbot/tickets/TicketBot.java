package org.golde.discordbot.tickets;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.command.support.SupportCommand;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.tickets.command.everyone.CommandTicket;
import org.golde.discordbot.tickets.tickets.TicketManager;

public class TicketBot extends ESSBot {

	public static void main(String[] args) throws Exception {
		new TicketBot().run();
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onReady() {
		TicketManager.loadFromFile(this);
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void onReload() {
		TicketManager.loadFromFile(this);
	}

	@Override
	public void registerEventListeners(List<EventBase> events) {
		events.add(new TicketManager.TicketManagerEvents(this));
		events.add(new EventManagerTicketEvent(this));
	}

	@Override
	public void registerEveryoneCommand(List<EveryoneCommand> cmds) {
		cmds.add(new CommandTicket(this));
	}

	@Override
	public void registerSupportCommand(List<SupportCommand> cmds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerChatModCommand(List<ChatModCommand> cmds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerGuildModCommand(List<GuildModCommand> cmds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerOwnerCommand(List<OwnerCommand> cmds) {
		// TODO Auto-generated method stub
		
	}

}
