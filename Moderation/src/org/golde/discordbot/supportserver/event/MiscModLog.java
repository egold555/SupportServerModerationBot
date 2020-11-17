package org.golde.discordbot.supportserver.event;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;

public class MiscModLog extends EventBase {
	
	public MiscModLog(ESSBot bot) {
		super(bot);
	}

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		Member member = event.getMember();
		log(event.getGuild(), member, member.getRoles(), event.getRoles(), true);
		
	}

	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		Member member = event.getMember();
		log(event.getGuild(), member, member.getRoles(), event.getRoles(), false);
	}

	private void log(Guild g, Member m, List<Role> currentRoles, List<Role> changedRoles, boolean added) {
		
		//youtube reaction role. 
		//TODO: Better way of doing this
		if(changedRoles.contains(g.getRoleById(667224721544183838L))) {
			return;
		}
		
		//Announcements role
		if(changedRoles.contains(g.getRoleById(728453778956877904L))) {
			return;
		}
		
		//Member role -- Ha rember this shit storm?!
		else if(changedRoles.contains(g.getRoleById(726526640313860236L))) {
			return;
		}
		//no need to log twice
		else if(changedRoles.contains(g.getRoleById(Roles.MUTED))) {
			return;
		}
		//Under 100 club
		else if(changedRoles.contains(g.getRoleById(735287621974097950L))) {
			return;
		}
		
		else if(changedRoles.contains(g.getRoleById(Roles.CODE_HELPER))) {
			return;
		}
		
		for(Role r : changedRoles) {
			if(r.getName().startsWith("Internal - ")) {
				return;
			}
		}
		
		MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
				bot,
				ModAction.ROLE_CHANGE, 
				g.getSelfMember().getUser(), 
				new String[][] {
					new String[] {"User: ", "<@" + m.getId() + ">"}, 
					new String[] {(added ? "Added" : "Removed") + " Roles:", getRoleListAsString(changedRoles)},
					new String[] {"Current Roles:", getRoleListAsString(currentRoles)}
				}
				);

		ModLog.log(g, actionEmbed);
	}
	
	//Not a great way of doing it, but it works and I am crunched for time atm
	private String getRoleListAsString(List<Role> roles) {
		
		List<String> names = new ArrayList<String>();
		
		if(roles == null || roles.size() == 0) {
			names.add("None");
		}
		else {
			for(Role r : roles) {
				
				if(r != null) {
					names.add(r.getName());
				}
				
			}
		}
		
		return String.join(", ", names);
		
		
	}

}
