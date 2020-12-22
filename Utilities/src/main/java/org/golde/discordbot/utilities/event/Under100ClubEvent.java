package org.golde.discordbot.utilities.event;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.event.EventBase;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

public class Under100ClubEvent extends EventBase {

	public Under100ClubEvent(ESSBot bot) {
		super(bot);
	}

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		Guild g = event.getGuild();
		
		Role under100ClubRole = g.getRoleById(735287621974097950L);
		
		Role memberRole = g.getRoleById(Roles.MEMBER);
		if(event.getRoles().contains(memberRole)) {
			
			event.getGuild().removeRoleFromMember(event.getMember(), under100ClubRole).queue();
			
		}
		
	}
	
	
}
