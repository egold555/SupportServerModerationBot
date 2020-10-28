package org.golde.discordbot.supportserver.command.chatmod;

import com.jagrosh.jdautilities.command.CommandEvent;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.supportserver.event.BlockedUrlsPreventer;

import java.util.List;

public class CommandBlockedUrls extends ChatModCommand {

    public CommandBlockedUrls(ESSBot bot) {
        super(bot, "blockedurls", null, "View all blocked URLs");
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        List<String> badUrls = BlockedUrlsPreventer.getUrls();
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String url : badUrls) {
            if (index == badUrls.size() - 1) {
                builder.append("•   ").append(url);
            } else {
                builder.append("•   ").append(url).append("\n");
            }
        }
        reply(event.getChannel(), "Blocked URLs", builder.toString());
    }

}
