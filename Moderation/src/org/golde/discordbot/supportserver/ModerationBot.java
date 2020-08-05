package org.golde.discordbot.supportserver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.command.support.SupportCommand;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.supportserver.command.chatmod.CommandMute;
import org.golde.discordbot.supportserver.command.chatmod.CommandPruneChat;
import org.golde.discordbot.supportserver.command.chatmod.CommandUnmute;
import org.golde.discordbot.supportserver.command.chatmod.CommandUserHistory;
import org.golde.discordbot.supportserver.command.chatmod.CommandWarn;
import org.golde.discordbot.supportserver.command.guildmod.CommandBan;
import org.golde.discordbot.supportserver.command.guildmod.CommandKick;
import org.golde.discordbot.supportserver.command.guildmod.CommandToggleRole;
import org.golde.discordbot.supportserver.command.owner.CommandRemoveAction;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.event.BannedUrlsChecker;
import org.golde.discordbot.supportserver.event.ClientInvitesNeedsToBeBetter;
import org.golde.discordbot.supportserver.event.DiscordMutePermsAreFucked;
import org.golde.discordbot.supportserver.event.IPGrabberPrevention;
import org.golde.discordbot.supportserver.event.IShouldNotEvenNeedThis;
import org.golde.discordbot.supportserver.event.MiscModLog;
import org.golde.discordbot.supportserver.event.StopChattingInTheWrongChannelsPls;
import org.golde.discordbot.supportserver.event.TryToFindIntrestingFiles;

import net.dv8tion.jda.api.entities.Activity;

public class ModerationBot extends ESSBot {

	public static void main(String[] args) throws Exception {
		new ModerationBot().run();
	}
	
	private static final Activity[] playingStatuses = new Activity[] {
			Activity.watching("Over Eric's Server"),
			Activity.watching("WALL-E"),
			Activity.listening("We Are The Robots - Kraftwerk"), 
			Activity.watching("The Matrix"),
			Activity.listening(";help"),
			Activity.watching("2001: A Space Odyssey"),
			Activity.playing("Robot Arena 2: Design and Destroy"),
			Activity.watching("BattleBots"),
			Activity.playing("I am open source! Check me out here: https://github.com/egold555/SupportServerModerationBot"),
			Activity.watching("2001: A Space Odyssey"),
			Activity.watching("The Terminator"),
			Activity.watching("For user submitted crash reports!"),
			Activity.playing("Oh look! Emma's here."),
			Activity.playing("Becoming more like HAL every day!"),
			Activity.playing("Eric's remember to replace this placeholder status text!"),
			Activity.playing("Chronologically inept since 2060."),
			Activity.playing("Fighting Ignorance since 1973 (It’s taking longer than we thought)."),
			Activity.playing("This is just a placeholder because I don't know what to put here."),
			Activity.playing("Haphazardly Spellchecked Since 2002."),
			Activity.playing("Putting The “i” Into “Teaim”."),
			};
	
	//randomize these messages
	static {
		List<Activity> statusList = Arrays.asList(playingStatuses);

		Collections.shuffle(statusList);

		statusList.toArray(playingStatuses);
	}
	
	private static int currentPlayingStatus = 0;

	

	@Override
	public String getPrefix() {
		return ";";
	}

	@Override
	public void onReady() {
//		new Timer().scheduleAtFixedRate(new TimerTask() {
//			
//			@Override
//			public void run() {
//				
//				if(currentPlayingStatus > playingStatuses.length - 1) {
//					currentPlayingStatus = 0;
//				}
//				
//				
//				
//				getJda().getPresence().setActivity(playingStatuses[currentPlayingStatus]);
//				
//				currentPlayingStatus++;
//				
//			}
//		}, 0, 60000);
		
		ClientInvitesNeedsToBeBetter.loadAlreadyUsedServers();
	}

	@Override
	public void onLoad() {
		Database.loadAllFromFile();
	}

	@Override
	public void onReload() {
		ClientInvitesNeedsToBeBetter.loadAlreadyUsedServers();
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
		
	}

	@Override
	public void registerEveryoneCommand(List<EveryoneCommand> cmds) {
		
	}

	@Override
	public void registerSupportCommand(List<SupportCommand> cmds) {
		
	}

	@Override
	public void registerChatModCommand(List<ChatModCommand> cmds) {
		cmds.add(new CommandMute(this));
		cmds.add(new CommandUnmute(this));
		cmds.add(new CommandWarn(this));
		cmds.add(new CommandPruneChat(this));
		cmds.add(new CommandUserHistory(this));
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

}
