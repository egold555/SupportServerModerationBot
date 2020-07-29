package org.golde.discordbot.supportserver.event;

import java.util.Timer;

import org.golde.discordbot.supportserver.constants.Roles;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PlayerCounter extends ListenerAdapter {

	Timer timer = new Timer();
	
	@Override
	public void onReady(ReadyEvent event) {
		
//		timer.schedule(new TimerTask() {
//			
//			@Override
//			public void run() {
//				updateChannel(Main.getGuild());
//			}
//		}, 1000 * 5, 1000 * 10);
		
		updateChannel(event.getJDA().getGuildById("594335572173258752"));
		
		
	}
	
	//Getting rate limited, going to update every 10 seconds
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		updateChannel(event.getGuild());
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		updateChannel(event.getGuild());
	}
	
	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		Role the100club = event.getGuild().getRoleById(735287621974097950L);
		
		if(event.getRoles().contains(the100club)) {
			updateChannel(event.getGuild());
		}
	}
//	
//	@Override
//	public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
//		updateChannel(event.getGuild());
//	}
	
	private void updateChannel(Guild g) {
		VoiceChannel vc = g.getVoiceChannelById("661108732838674445");
		//int[] rawData = getMemberTotalCount(g);
		
		//vc.getManager().setName("Online: " + rawData[1] + " / " + rawData[0]).queue();
		vc.getManager().setName("Total Members: " + getTotalMemberCount(g)).queue();
	}
	
	private int[] getMemberTotalCount(Guild g) {
		int am[] = {0, 0};
		for(Member m : g.getMemberCache().asSet()) {
			
			if(!m.getUser().isBot()) {
				am[0]++;
				if(m.getOnlineStatus() != OnlineStatus.OFFLINE && m.getOnlineStatus() != OnlineStatus.INVISIBLE && m.getOnlineStatus() != OnlineStatus.UNKNOWN) {
					am[1]++;
				}
			}
		}
		return am;
	}
	
	private int getTotalMemberCount(Guild g) {
		int toReturn = 0;
		
		for(Member m : g.getMembersWithRoles(g.getRoleById(Roles.MEMBER))) {
			if(!m.getUser().isBot() && !m.getUser().isFake()) {
				toReturn++;
			}
		}
		return toReturn;
	}
	
}
