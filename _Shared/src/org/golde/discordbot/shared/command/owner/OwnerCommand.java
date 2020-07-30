package org.golde.discordbot.shared.command.owner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.BaseCommand;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public abstract class OwnerCommand extends BaseCommand {
	
	private static final Category CATEGORY_OWNER = new Category("\u2728 Administrator");
	
	public OwnerCommand(@Nonnull ESSBot bot, @Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		
		super(bot, nameIn, argsIn, helpIn, aliasesIn);

		this.category = CATEGORY_OWNER;
		this.requiredRole = CATEGORY_OWNER.getName();
	}
	
	public final static boolean isOwner(Member person) {
		for(Role r : person.getRoles()) {
			if(r.getName().equals(CATEGORY_OWNER.getName())) {
				return true;
			}
		}
		return false;
	}
	
}
