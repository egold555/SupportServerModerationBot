package org.golde.discordbot.supportserver;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.db.AbstractDBTranslation;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.supportserver.command.chatmod.*;
import org.golde.discordbot.supportserver.command.guildmod.CommandBan;
import org.golde.discordbot.supportserver.command.guildmod.CommandKick;
import org.golde.discordbot.supportserver.command.guildmod.CommandToggleRole;
import org.golde.discordbot.supportserver.command.owner.CommandRemoveAction;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.event.BannedUrlsChecker;
import org.golde.discordbot.supportserver.event.BlockedUrlsPreventer;
import org.golde.discordbot.supportserver.event.ClientInvitesNeedsToBeBetter;
import org.golde.discordbot.supportserver.event.DiscordMutePermsAreFucked;
import org.golde.discordbot.supportserver.event.IHateKids2point0;
import org.golde.discordbot.supportserver.event.IPGrabberPrevention;
import org.golde.discordbot.supportserver.event.IShouldNotEvenNeedThis;
import org.golde.discordbot.supportserver.event.MiscModLog;
import org.golde.discordbot.supportserver.event.StopChattingInTheWrongChannelsPls;
import org.golde.discordbot.supportserver.event.TryToFindIntrestingFiles;

public class ModerationBot extends ESSBot {

	public static void main(String[] args) throws Exception {
		new ModerationBot().run();
	}

	@Override
	public String getPrefix() {
		return ".";
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void registerEventListeners(List<EventBase> events) {
		events.add(new IPGrabberPrevention(this));
		events.add(new BannedUrlsChecker(this));
		events.add(new IShouldNotEvenNeedThis(this));
		events.add(new MiscModLog(this));
		events.add(new TryToFindIntrestingFiles(this));
		events.add(new StopChattingInTheWrongChannelsPls(this));
		events.add(new ClientInvitesNeedsToBeBetter(this));
		events.add(new DiscordMutePermsAreFucked(this));
		events.add(new IHateKids2point0(this));
		events.add(new BlockedUrlsPreventer(this));
	}

	@Override
	public void registerChatModCommand(List<ChatModCommand> cmds) {
		cmds.add(new CommandMute(this));
		cmds.add(new CommandUnmute(this));
		cmds.add(new CommandWarn(this));
		cmds.add(new CommandPruneChat(this));
		cmds.add(new CommandUserHistory(this));
		cmds.add(new CommandAddBlockedUrl(this));
		cmds.add(new CommandBlockedUrls(this));
		cmds.add(new CommandRemoveBlockedUrl(this));
	}

	@Override
	public void registerGuildModCommand(List<GuildModCommand> cmds) {
		cmds.add(new CommandKick(this));
		cmds.add(new CommandBan(this));
		cmds.add(new CommandToggleRole(this));
	}

	@Override
	public void registerOwnerCommand(List<OwnerCommand> cmds) {
		cmds.add(new CommandRemoveAction(this));
		//cmds.add(new CommandTest(this, getWaiter()));
	}
	
	@Override
	public void registerDatabaseTranslations(List<Class<? extends AbstractDBTranslation>> dbt) {
		dbt.add(Offence.class);
		super.registerDatabaseTranslations(dbt);
	}

}
