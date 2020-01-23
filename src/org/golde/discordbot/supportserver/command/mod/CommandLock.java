package org.golde.discordbot.supportserver.command.mod;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.supportserver.util.Channels;
import org.golde.discordbot.supportserver.util.CustomEmotes;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;
import org.golde.discordbot.supportserver.util.Roles;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class CommandLock extends ModCommand {

	public CommandLock() {
		this.name = "lock";
		this.help = "Attempt to mitigate botting as much as we can";
	}
	
	public static boolean locked = false;
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		Guild g = event.getGuild();

		//only people with phone verification can join. Try to prevent the bots from joining....
		g.getManager().setVerificationLevel(VerificationLevel.HIGH).queue();
		
		g.getTextChannelById(Channels.ANNOUNCEMENTS).sendMessage(CustomEmotes.RED_ALERT + " Server has entered lockdown mode.").queue();
		
		List<GuildChannel> channelsToModify = getChannels(g);
		
		for(GuildChannel gc : channelsToModify) {
			
			if(gc instanceof TextChannel) {
				PermissionOverride permissionOverride = gc.getPermissionOverride(Roles.EVERYONE.getRole());
				PermissionOverrideAction manager;
				if(permissionOverride == null) {
					manager = gc.createPermissionOverride(Roles.EVERYONE.getRole());
				}
				else {
					manager = permissionOverride.getManager();
				}

	            manager.deny(Permission.MESSAGE_WRITE).queue();
			}
			else if(gc instanceof VoiceChannel){
				
			}
			
		}
		ModLog.log(g, ModLog.getActionTakenEmbed(ModAction.LOCK, event.getAuthor()));
		locked = true;
		
	}

	public static List<GuildChannel> getChannels(Guild g){

		List<GuildChannel> toReturn = new ArrayList<GuildChannel>();

		//Discussion
		for(GuildChannel c : g.getCategoryById("594335572173258753").getChannels()) {

			if(!c.getName().equalsIgnoreCase("leak-lounge")) {
				toReturn.add(c);
			}

		}

		//User Stuff & Things
		for(GuildChannel c : g.getCategoryById("635964206251900956").getChannels()) {
			toReturn.add(c);
		}

		//Misc
		for(GuildChannel c : g.getCategoryById("644005865103556608").getChannels()) {
			toReturn.add(c);
		}

		return toReturn;

	}



}
