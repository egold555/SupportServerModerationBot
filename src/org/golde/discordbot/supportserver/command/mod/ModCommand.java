package org.golde.discordbot.supportserver.command.mod;

import org.golde.discordbot.supportserver.command.BaseCommand;
import org.golde.discordbot.supportserver.util.StringUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class ModCommand extends BaseCommand {

	public ModCommand() {
		this.category = CATEGORY_MODERATOR;
		this.requiredRole = "Chat Moderator";
	}
	
	protected final boolean isModerator(Member person) {
		for(Role r : person.getRoles()) {
			if(r.getName().toLowerCase().contains("mod")) {
				return true;
			}
		}
		return false;
	}
	
	
}
