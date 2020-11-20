package org.golde.discordbot.supportserver.event;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.shared.event.AbstractMessageChecker;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public class BlockedUrlsPreventer extends AbstractMessageChecker {

	public BlockedUrlsPreventer(ESSBot bot) {
		super(bot);
	}

	private static final String FILE_NAME = "blocked-urls";
	private static List<String> BAD_URLS;
	
	static {
		reload();
	}

	
	public static void reload() {
		BAD_URLS = FileUtil.loadArrayFromFile(FILE_NAME, String[].class);
	}

	public static void addUrl(String url) {
		BAD_URLS.add(url);
		FileUtil.saveToFile(BAD_URLS, FILE_NAME);
	}

	public static List<String> getUrls() {
		reload();
		return BAD_URLS;
	}
	
	public static boolean remove(String url) {
		boolean success = BAD_URLS.remove(url);
		if(success) {
			FileUtil.saveToFile(BAD_URLS, FILE_NAME);
		}
		return success;
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

		if(target == null || target.getUser() == null || target.getUser().isBot() || target.getUser().isFake()) {
			return;
		}
		
		for(Role r : target.getRoles()) {
			if(r.getIdLong() == Roles.CHAT_MODERATOR) {
				return;
			}
		}
		
		//delete their message
		msg.delete().queue();

		//send them a message
		replyError(msg.getChannel(), "That linked is blocked on this server. Please contact a Moderator if you think this is a mistake.", 10);

	}

}
