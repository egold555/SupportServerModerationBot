package org.golde.discordbot.supportserver.command.chatmod.old;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class CommandUnlock extends ChatModCommand {

	public CommandUnlock(@Nonnull ESSBot bot) {
		super(bot, "unlock", null, "Unlocks a given text channel");
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		Guild g = event.getGuild();

		TextChannel tc = event.getTextChannel();
		
		if(!CommandLock.canLock(g, tc)) {
			replyError(tc, "That channel is protected and can not be locked.");
			return;
		}
		
		PermissionOverride permissionOverride = tc.getPermissionOverride(g.getRoleById(Roles.EVERYONE));
		PermissionOverrideAction manager;
		if(permissionOverride == null) {
			manager = tc.createPermissionOverride(g.getRoleById(Roles.EVERYONE));
		}
		else {
			manager = permissionOverride.getManager();
		}

        manager.grant(Permission.MESSAGE_WRITE).queue();
		ModLog.log(g, ModLog.getActionTakenEmbed(bot, ModAction.UNLOCK, event.getAuthor(), new String[] {
				"Channel",
				tc.getAsMention()
		}));
		
		replySuccess(tc, "Success!");
	}

}
