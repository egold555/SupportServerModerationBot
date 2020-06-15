package org.golde.discordbot.supportserver.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.constants.Roles;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoRemoveBirthdayRole extends ListenerAdapter {

	HashMap<Long, Long> peopleWhoHaveTheRole = new HashMap<Long, Long>();
	
	public AutoRemoveBirthdayRole() {
		
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				tryClearExpiredRoles();
			}
		}, 0, 1000 * 60);
		
	}
	
	protected void tryClearExpiredRoles() {
		for(long userId : peopleWhoHaveTheRole.keySet()) {
			if((peopleWhoHaveTheRole.get(userId) + 86400000) < System.currentTimeMillis()) {
				Main.getGuild().removeRoleFromMember(userId, Main.getGuild().getRoleById(Roles.ITS_MY_BDAY)).queue();
				peopleWhoHaveTheRole.remove(userId);
			}
			
		}
	}

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		
		for(Role r : event.getRoles()) {
			
			if(r.getIdLong() == Roles.ITS_MY_BDAY) {
				peopleWhoHaveTheRole.put(event.getMember().getIdLong(), System.currentTimeMillis());
			}
			
		}
		
	}
	
}
