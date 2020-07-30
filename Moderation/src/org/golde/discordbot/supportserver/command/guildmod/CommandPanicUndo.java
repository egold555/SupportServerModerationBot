package org.golde.discordbot.supportserver.command.guildmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class CommandPanicUndo extends GuildModCommand {

	public CommandPanicUndo(@Nonnull ESSBot bot) {
		super(bot, "panicisover", null, "Were good boss, no need to panic anymore");
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		Guild g = event.getGuild();
		TextChannel tc = event.getTextChannel();

		//now everything is good. Try to prevent the bots from joining....
		g.getManager().setVerificationLevel(VerificationLevel.NONE).queue();
		
		g.getTextChannelById(Channels.ANNOUNCEMENTS).sendMessage(SSEmojis.RED_ALERT + " Server has been released from lockdown mode.").queue();
		
		List<GuildChannel> channelsToModify = CommandPanic.getChannels(g);
		
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

	            manager.grant(Permission.MESSAGE_WRITE).queue();
			}
			
			
		}
		
		ModLog.log(g, ModLog.getActionTakenEmbed(bot, ModAction.UNLOCK, event.getAuthor()));
		CommandPanic.locked = false;
		
		replySuccess(tc, "Success!");
	}

}
