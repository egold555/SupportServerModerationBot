package org.golde.discordbot.supportserver.event;

import java.util.List;
import java.util.Set;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.event.AbstractMessageChecker;
import org.golde.discordbot.shared.util.FileUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class BannedUrlsChecker extends AbstractMessageChecker {

	public BannedUrlsChecker(ESSBot bot) {
		super(bot);
	}

	private static final List<String> BAD_URLS;
	
	static {
		BAD_URLS = FileUtil.readGenericConfig("banned-urls");
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

		
		replyError(msg.getChannel(), target.getAsMention() + ", That URL is blocked on this server. Please don't send it :)");
		
		//delete their message
		msg.delete().queue();

	



	}
}
