package org.golde.discordbot.supportserver.event;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.event.EventBase;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

public class AutoRemoveBirthdayRole extends EventBase {

	HashMap<Long, Long> peopleWhoHaveTheRole = new HashMap<Long, Long>();
	
	public AutoRemoveBirthdayRole(@Nonnull ESSBot bot) {
		super(bot);
		
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
				bot.getGuild().removeRoleFromMember(userId, bot.getGuild().getRoleById(Roles.ITS_MY_B_DAY)).queue();
				peopleWhoHaveTheRole.remove(userId);
			}
			
		}
	}

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		
		for(Role r : event.getRoles()) {
			
			if(r.getIdLong() == Roles.ITS_MY_B_DAY) {
				peopleWhoHaveTheRole.put(event.getMember().getIdLong(), System.currentTimeMillis());
			}
			
		}
		
	}
	
}
