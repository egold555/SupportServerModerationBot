package org.golde.discordbot.supportserver.command.owner;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.supportserver.event.BlockedUrlsPreventer;

import java.util.List;

public class CommandRemoveBlockedUrl extends OwnerCommand {

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
        
        boolean success = BlockedUrlsPreventer.remove(url);
        if(success) {
        	replySuccess(tc, "Removed **" + url + "** from the blocked urls list.");
        }
        else {
        	replyError(tc, "Failed to remove **" + url + "** from the blocked urls list. Does it exist?");
        }
    }

}
