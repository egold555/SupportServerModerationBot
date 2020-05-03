package org.golde.discordbot.supportserver.command.mod;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.supportserver.constants.Categories;
import org.golde.discordbot.supportserver.constants.Roles;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class CommandLock extends ModCommand {

	public CommandLock() {
		super("lock", "[channel]", "Lock a given channel");
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		Guild g = event.getGuild();
		TextChannel tc = event.getTextChannel();
		
		
		if(args.size() != 0) {
			List<TextChannel> gotten = g.getTextChannelsByName(args.get(0), true);
			if(gotten.size() == 0) {
				event.replyError("No channel found!");
				return;
			}
			else {
				tc = gotten.get(0);
			}
		}
		
		if(!canLock(g, tc)) {
			event.replyError("That channel is protected and can not be locked.");
			return;
		}
		
		PermissionOverride permissionOverride = tc.getPermissionOverride(Roles.EVERYONE.getRole());
		PermissionOverrideAction manager;
		if(permissionOverride == null) {
			manager = tc.createPermissionOverride(Roles.EVERYONE.getRole());
		}
		else {
			manager = permissionOverride.getManager();
		}

        manager.deny(Permission.MESSAGE_WRITE).queue();
		ModLog.log(g, ModLog.getActionTakenEmbed(ModAction.LOCK, event.getAuthor(), new String[] {
				"Channel",
				tc.getAsMention()
		}));
		
		event.replySuccess("Success!");
		
	}

	static boolean canLock(Guild g, TextChannel tc){

		List<GuildChannel> channels = new ArrayList<GuildChannel>();

		//Discussion
		for(GuildChannel c : g.getCategoryById(Categories.DISCUSSION).getChannels()) {

			if(!c.getName().equalsIgnoreCase("leak-lounge")) {
				channels.add(c);
			}

		}

		//User Stuff & Things
		for(GuildChannel c : g.getCategoryById(Categories.USER_STUFF_AND_THING).getChannels()) {
			channels.add(c);
		}

		//Misc
		for(GuildChannel c : g.getCategoryById(Categories.MISC).getChannels()) {
			channels.add(c);
		}

		return channels.contains(tc);

	}



}
