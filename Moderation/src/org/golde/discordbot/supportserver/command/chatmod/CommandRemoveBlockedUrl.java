package org.golde.discordbot.supportserver.command.chatmod;

import com.jagrosh.jdautilities.command.CommandEvent;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.supportserver.event.BlockedUrlsPreventer;

import java.util.List;

public class CommandRemoveBlockedUrl extends ChatModCommand {

    public CommandRemoveBlockedUrl(ESSBot bot) {
        super(bot, "unblockurl", "<url>", "Unblock a given url");
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        TextChannel tc = event.getTextChannel();
        String url = args.get(1);

        if(args.size() != 2) {
            replyError(tc, getHelpReply());
            return;
        }
        BlockedUrlsPreventer.remove(url);
        replySuccess(tc, "Removed **" + url + "** from the blocked urls list.");
    }

}
