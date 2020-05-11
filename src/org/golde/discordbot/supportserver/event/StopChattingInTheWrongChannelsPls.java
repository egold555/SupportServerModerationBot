package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.supportserver.command.BaseCommand;
import org.golde.discordbot.supportserver.constants.Channels;

import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class StopChattingInTheWrongChannelsPls extends AbstractMessageChecker {

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		String text = msg.getContentRaw();
		if(msg.getChannel().getIdLong() == Channels.USER_MADE_SNIPPETS) {

			if(!text.toLowerCase().contains("name:") || !text.toLowerCase().contains("description:")) {
				return true;
			}

		}

		if(msg.getChannel().getIdLong() == Channels.CLIENT_INVITES) {

			return !text.toLowerCase().contains("https://discord.");

		}

		return false;
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		BaseCommand.sendSelfDestructingMessage(msg.getChannel(), 2, "Please don't chat in here " + EmojiParser.parseToUnicode(":smile:"), success -> {
			msg.delete().queue();
		});
		
		
	}

}
