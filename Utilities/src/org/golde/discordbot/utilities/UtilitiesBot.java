package org.golde.discordbot.utilities;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.command.support.SupportCommand;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.utilities.command.everyone.CommandCommonError;
import org.golde.discordbot.utilities.crash.CrashReportEventHandler;
import org.golde.discordbot.utilities.reactionroles.ReactionRolesListener;
import org.golde.discordbot.utilities.usercount.PlayerCounter;

public class UtilitiesBot extends ESSBot {

	public static void main(String[] args) throws Exception {
		new UtilitiesBot().run();
	}
	
	@Override
	public String getPrefix() {
		return ",";
	}

	@Override
	public void onReady() {
		
	}

	@Override
	public void onLoad() {
		
	}
	
	@Override
	public void onReload() {
		CrashReportEventHandler.reloadDB();
	}
	
	@Override
	public void registerEventListeners(List<EventBase> events) {
		events.add(new CrashReportEventHandler(this));
		events.add(new ReactionRolesListener(this));
		events.add(new PlayerCounter(this));
	}

	@Override
	public void registerEveryoneCommand(List<EveryoneCommand> cmds) {
		cmds.add(new CommandCommonError(this));
	}

	@Override
	public void registerSupportCommand(List<SupportCommand> cmds) {
		
	}

	@Override
	public void registerChatModCommand(List<ChatModCommand> cmds) {
		
	}

	@Override
	public void registerGuildModCommand(List<GuildModCommand> cmds) {
		
	}

	@Override
	public void registerOwnerCommand(List<OwnerCommand> cmds) {
		
	}

}
