package org.golde.discordbot.supportserver.command.owner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.golde.discordbot.supportserver.command.BaseCommand;

import com.jagrosh.jdautilities.command.Command.Category;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public abstract class OwnerCommand extends BaseCommand {
	
	private static final Category CATEGORY_OWNER = new Category("✨ Administrator");
	
	public OwnerCommand(@Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		
		super(nameIn, argsIn, helpIn, aliasesIn);

		this.category = CATEGORY_OWNER;
		this.requiredRole = CATEGORY_OWNER.getName();
	}
	
	protected final boolean isOwner(Member person) {
		for(Role r : person.getRoles()) {
			if(r.getName().equals(CATEGORY_OWNER.getName())) {
				return true;
			}
		}
		return false;
	}
	
}
