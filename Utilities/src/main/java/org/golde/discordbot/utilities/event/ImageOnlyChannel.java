package org.golde.discordbot.utilities.event;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.event.AbstractMessageChecker;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.shared.db.ICanHasDatabaseFile;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class ImageOnlyChannel extends AbstractMessageChecker implements ICanHasDatabaseFile {

	private static List<Long> IDS = new ArrayList<Long>();
	
	@Override
	public void reload() {
		IDS.clear();
		IDS = FileUtil.loadArrayFromFile("image-only-channels", Long[].class);
	}
	
	public ImageOnlyChannel(ESSBot bot) {
		super(bot);
	}

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		
		TextChannel tc = msg.getTextChannel();
		if(IDS.contains(tc.getIdLong())) {
			
			for(Role r : sender.getRoles()) {
				if(r.getIdLong() == Roles.CHAT_MODERATOR) {
					return false;
				}
			}
			
			if(msg.getAttachments().size() == 0) {
				return true;
			}
			
			for(Attachment a : msg.getAttachments()) {
				if(a.isVideo()) {
					return true;
				}
			}
			
		}
		
		return false;
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		msg.delete().queue();
		replyError(msg.getChannel(), "Only images are allowed in this channel.", 10);
	}

}
