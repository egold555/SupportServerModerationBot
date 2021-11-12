package org.golde.discordbot.supportserver.event;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.shared.event.AbstractMessageChecker;
import org.golde.discordbot.shared.util.DateUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class IPGrabberPrevention extends AbstractMessageChecker {

	public IPGrabberPrevention(ESSBot bot) {
		super(bot);
	}

	private static final List<String> BAD_URLS;
	
	static {
		BAD_URLS = FileUtil.readGenericConfig("ip-logger-urls");
	}


	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		String text = msg.getContentStripped();
		for(String bad : BAD_URLS) {
			if(text.toLowerCase().contains(bad.toLowerCase())) {
				return true;
			}
		}

		return false;

	}

	//target is null if its a webhook
	//Fix
	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {

		//delete their message
		msg.delete().queue();

		MuteManager.muteUser(bot, target.getIdLong(), guild.getSelfMember().getIdLong(), "[Auto Mute] - IP Grabber Link", DateUtil.parseDateDiff("1h", true));
		
	}

}
