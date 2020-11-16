package org.golde.discordbot.supportserver.command.guildmod.old;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.constants.Categories;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class CommandPanic extends GuildModCommand {

	public CommandPanic(@Nonnull ESSBot bot) {
		super(bot, "panic", null, "Attempt to mitigate botting as much as we can");
	}
	
	public static boolean locked = false;
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		Guild g = event.getGuild();

		TextChannel tc = event.getTextChannel();
		
		//only people with phone verification can join. Try to prevent the bots from joining....
		g.getManager().setVerificationLevel(VerificationLevel.HIGH).queue();
		
		g.getTextChannelById(Channels.Info.ANNOUNCEMENTS).sendMessage(SSEmojis.RED_ALERT + " Server has entered lockdown mode.").queue();
		
		List<GuildChannel> channelsToModify = getChannels(g);
		
		for(GuildChannel gc : channelsToModify) {
			
			if(gc instanceof TextChannel) {
				PermissionOverride permissionOverride = gc.getPermissionOverride(g.getRoleById(Roles.MEMBER));
				PermissionOverrideAction manager;
				if(permissionOverride == null) {
					manager = gc.createPermissionOverride(g.getRoleById(Roles.MEMBER));
				}
				else {
					manager = permissionOverride.getManager();
				}

	            manager.deny(Permission.MESSAGE_WRITE).queue();
			}
			else if(gc instanceof VoiceChannel){
				
			}
			
		}
		ModLog.log(g, ModLog.getActionTakenEmbed(bot, ModAction.LOCK, event.getAuthor()));
		locked = true;
		
		replySuccess(tc, "Success!");
		
	}

	public static List<GuildChannel> getChannels(Guild g){

		List<GuildChannel> toReturn = new ArrayList<GuildChannel>();
		
		long[] NON_SPECIAL_CASES = new long[] {
				Categories.USER_CONTRIBUTIONS,
				Categories.MISCELLANEOUS_CHATS,
		};

		//Discussion
		for(GuildChannel c : g.getCategoryById(Categories.DISCUSSION).getChannels()) {

			if(!c.getName().equalsIgnoreCase("leak-lounge")) {
				toReturn.add(c);
			}

		}

		for(long categoryId : NON_SPECIAL_CASES) {
			for(GuildChannel c : g.getCategoryById(categoryId).getChannels()) {
				toReturn.add(c);
			}
		}

		return toReturn;

	}



}
