package org.golde.discordbot.shared.command.guildmod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.BaseCommand;
import org.golde.discordbot.shared.constants.Roles;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public abstract class GuildModCommand extends BaseCommand {

	private static final Category CATEGORY_GUILD_MODERATOR = new Category(Roles.GUILD_MODERATOR);
	
	public GuildModCommand(@Nonnull ESSBot bot, @Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		super(bot, nameIn, argsIn, helpIn, aliasesIn);
		this.category = CATEGORY_GUILD_MODERATOR;
		this.requiredRole = CATEGORY_GUILD_MODERATOR.getRoleIdLong();
	}
	
	public final static boolean isModerator(Member person) {
		for(Role r : person.getRoles()) {
			if(r.getIdLong() == CATEGORY_GUILD_MODERATOR.getRoleIdLong()) {
				return true;
			}
		}
		return false;
	}
	
	
}
