package org.golde.discordbot.supportserver.command.owner;

import org.golde.discordbot.supportserver.command.BaseCommand;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public abstract class OwnerCommand extends BaseCommand {

	public OwnerCommand() {
		this.category = CATEGORY_OWNER;
		this.requiredRole = "Founder";
	}
	
	protected final boolean isOwner(Member person) {
		for(Role r : person.getRoles()) {
			if(r.getName().toLowerCase().contains("founder")) {
				return true;
			}
		}
		return false;
	}
	
}
