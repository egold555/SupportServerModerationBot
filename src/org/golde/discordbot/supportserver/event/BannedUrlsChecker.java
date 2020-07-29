package org.golde.discordbot.supportserver.event;

import java.util.Set;

import org.golde.discordbot.supportserver.constants.Roles;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.FileUtil;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class BannedUrlsChecker extends AbstractMessageChecker {

	private static final Set<String> BAD_URLS;
	
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
