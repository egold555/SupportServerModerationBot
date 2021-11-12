package org.golde.discordbot.supportserver.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.event.AbstractMessageChecker;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class EventDenyFileUploadsAndLinks extends AbstractMessageChecker {

	private static final String URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	
	public EventDenyFileUploadsAndLinks(ESSBot bot) {
		super(bot);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		if(!sender.getRoles().contains(sender.getGuild().getRoleById(Roles.INTERNAL_NO_FILE_UPLOADS))) {
			return false;
		}

		if(msg.getAttachments().size() > 0) {
			return true;
		}

		Pattern pattern = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = pattern.matcher(msg.getContentStripped());
		
		return urlMatcher.find();

	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		
		replyError(msg.getChannel(), target.getAsMention() + " You are temporarly denied from sending any links or files. If you think this is a mistake, please contact a Moderator.", 20);
		msg.delete().queue();
		
	}

}
