package org.golde.discordbot.supportserver.event;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class YRDShanagans extends AbstractMessageChecker {

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		
		String text = msg.getContentStripped().toLowerCase();
		
		if(msg.getChannel().getIdLong() != 720712103081803876L) {
			return text.contains("yrd");
		}
		
		return false;
		
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		msg.delete().queue();
		reply(msg.getChannel(), "YRD Patrol", "Please use " + guild.getTextChannelById(720712103081803876L).getAsMention() + " for saying 'YRD'. I am trying to not clog up other channels.", 5);
	}

}
