package org.golde.discordbot.supportserver.command.mod;

import java.util.List;

import org.golde.discordbot.supportserver.util.Channels;
import org.golde.discordbot.supportserver.util.CustomEmotes;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.Roles;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class CommandUnlock extends ModCommand {

	public CommandUnlock() {
		this.name = "unlock";
		this.help = "Attempt to mitigate botting as much as we can";
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		Guild g = event.getGuild();

		//now everything is good. Try to prevent the bots from joining....
		g.getManager().setVerificationLevel(VerificationLevel.NONE).queue();
		
		g.getTextChannelById(Channels.ANNOUNCEMENTS).sendMessage(CustomEmotes.RED_ALERT + " Server has been released from lockdown mode.").queue();
		
		List<GuildChannel> channelsToModify = CommandLock.getChannels(g);
		
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

	            manager.grant(Permission.MESSAGE_WRITE).queue();
			}
			
			
		}
		
		ModLog.log(g, ModLog.getActionTakenEmbed(ModAction.UNLOCK, event.getAuthor()));
		CommandLock.locked = false;
	}

}
