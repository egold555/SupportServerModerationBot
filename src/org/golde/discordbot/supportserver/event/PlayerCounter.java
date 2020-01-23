package org.golde.discordbot.supportserver.event;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PlayerCounter extends ListenerAdapter {

	@Override
	public void onReady(ReadyEvent event) {
		Guild g = event.getJDA().getGuilds().get(0); //were only in one guild
		updateChannel(g);
	}
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		updateChannel(event.getGuild());
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		updateChannel(event.getGuild());
	}
	
	private void updateChannel(Guild g) {
		VoiceChannel vc = g.getVoiceChannelById("661108732838674445");
		int[] rawData = getMemberTotalCount(g);
		
		vc.getManager().setName("Online: " + rawData[1] + " / " + rawData[0]).queue();
	}
	
	private int[] getMemberTotalCount(Guild g) {
		int am[] = {0, 0};
		for(Member m : g.getMembers()) {
			
			if(!m.getUser().isBot()) {
				am[0]++;
				if(m.getOnlineStatus() != OnlineStatus.OFFLINE && m.getOnlineStatus() != OnlineStatus.INVISIBLE && m.getOnlineStatus() != OnlineStatus.UNKNOWN) {
					am[1]++;
				}
			}
		}
		return am;
	}
	
}
