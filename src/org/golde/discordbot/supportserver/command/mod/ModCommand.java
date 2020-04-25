package org.golde.discordbot.supportserver.command.mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.golde.discordbot.supportserver.command.BaseCommand;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public abstract class ModCommand extends BaseCommand {

	public ModCommand(@Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		super(nameIn, argsIn, helpIn, aliasesIn);
		this.category = CATEGORY_MODERATOR;
		this.requiredRole = "Chat Moderator";
	}
	
	public final static boolean isModerator(Member person) {
		for(Role r : person.getRoles()) {
			if(r.getName().toLowerCase().contains("mod")) {
				return true;
			}
		}
		return false;
	}
	
	
}
