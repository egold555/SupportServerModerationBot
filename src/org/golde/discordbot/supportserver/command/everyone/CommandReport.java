package org.golde.discordbot.supportserver.command.everyone;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sun.org.apache.xpath.internal.objects.XNull;
import com.sun.org.apache.xpath.internal.operations.Mod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.golde.discordbot.supportserver.command.BaseCommand;
import org.golde.discordbot.supportserver.command.mod.ModCommand;
import org.golde.discordbot.supportserver.util.ModLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommandReport extends BaseCommand {


    public CommandReport()
    {
        this.guildOnly = true;
        this.name = "Report";
        this.help = "Reports To Server!";
        this.arguments = "<player> [reason]";
        this.aliases = new String[]{"r", "report"};
    }



    @Override
    protected void execute(CommandEvent event, List<String> args) {


        Member member = event.getMember();

        if(event.getArgs().isEmpty())
        {
            event.replyError("Please provide the name of a player to report!");
            return;
        }
        else {

            Member selfMember = event.getGuild().getSelfMember();
            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

            if (args.isEmpty() || mentionedMembers.isEmpty()) {
                event.replyError("Missing arguments");
                return;
            }

            Member target = mentionedMembers.get(0);
            String reason = String.join(" ", args.subList(2, args.size()));

            if (!selfMember.canInteract(target)) {
                event.replyError("I can't report that user! Sorry!");
                return;
            }

            if(reason == null || reason.isEmpty()) {
                reason = "No reason provided.";
            }

            final String reasonFinal = reason;
            MessageEmbed actionEmbed = ModLog.report(ModLog.ModAction.REPORT,event.getAuthor(), target, reason, System.currentTimeMillis()); //getActionTakenEmbed(ModLog.ModAction.REPORT,event.getAuthor(), target, reason);
            ModLog.log(event.getGuild(), actionEmbed);
            event.replySuccess("Success! Reported the user!");
        }
    }
}
