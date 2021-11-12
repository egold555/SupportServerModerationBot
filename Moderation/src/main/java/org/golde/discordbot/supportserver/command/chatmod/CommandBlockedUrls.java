package org.golde.discordbot.supportserver.command.chatmod;

import com.jagrosh.jdautilities.command.CommandEvent;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.supportserver.event.BlockedUrlsPreventer;

import java.util.List;

public class CommandBlockedUrls extends ChatModCommand {

	private static final int MAX_PER_EMBED = 75;
	
    public CommandBlockedUrls(ESSBot bot) {
        super(bot, "blockedurls", null, "View all blocked URLs");
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        List<String> badUrls = BlockedUrlsPreventer.getUrls();
        StringBuilder builder = new StringBuilder();
        int index = 0;
        int count = 1;
        int page = 0;
        for (String url : badUrls) {
            if (index == badUrls.size() - 1) {
                builder.append("•   ").append(url);
            } else {
                builder.append("•   ").append(url).append("\n");
            }
            
            if(count > MAX_PER_EMBED) {
            	count = 0;
            	page++;
            	reply(event.getChannel(), "Blocked URLs (Page " + page + ")", builder.toString());
            	builder = new StringBuilder();
            }
            count++;
        }
        page++;
        reply(event.getChannel(), "Blocked URLs (Page " + page + ")", builder.toString());
    }

}
