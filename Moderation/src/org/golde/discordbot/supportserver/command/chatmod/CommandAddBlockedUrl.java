package org.golde.discordbot.supportserver.command.chatmod;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.supportserver.event.BlockedUrlsPreventer;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;

public class CommandAddBlockedUrl extends ChatModCommand {

	public CommandAddBlockedUrl(ESSBot bot) {
		super(bot, "blockurl", "<url>", "Block a given url", "blocku, bu");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		TextChannel tc = event.getTextChannel();
		String url = args.get(1);
		
		if(args.size() != 2) {
			replyError(tc, getHelpReply());
			return;
		}
		BlockedUrlsPreventer.addUrl(url);
		replySuccess(tc, "Added **" + url + "** to the blocked urls list.");
	}

}
