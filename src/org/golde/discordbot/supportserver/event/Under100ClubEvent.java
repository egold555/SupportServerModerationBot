package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.supportserver.constants.Roles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

public class Under100ClubEvent extends EventBase {

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
