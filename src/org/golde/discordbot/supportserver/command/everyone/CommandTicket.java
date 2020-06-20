package org.golde.discordbot.supportserver.command.everyone;

import java.util.List;

import org.golde.discordbot.supportserver.tickets.TicketManager;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandTicket extends EveryoneCommand {

	public CommandTicket() {
		super("ticket", "[close, add, remove, public, private]", "Ticket management command", "t");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		if(args.size() < 2) {
			event.replyError(this.getHelpReply());
		}
		else {
			Member sender = event.getMember();
			TextChannel tc = event.getTextChannel();
			String subCmd = args.get(1);
			
			if(subCmd.equalsIgnoreCase("close")) {
				TicketManager.close(sender, tc);
			}
			else if(subCmd.equalsIgnoreCase("add")) {
				TicketManager.addMember(sender, tc, event.getMessage().getMentionedMembers());
			}
			else if(subCmd.equalsIgnoreCase("remove")) {
				TicketManager.removeMember(sender, tc, event.getMessage().getMentionedMembers());
			}
			else if(subCmd.equalsIgnoreCase("public")) {
				TicketManager.makePublic(sender, tc);
			}
			else if(subCmd.equalsIgnoreCase("private")) {
				TicketManager.makePrivate(sender, tc);
			}
//			else if(subCmd.equalsIgnoreCase("genhtml")) {
//				TicketManager.genHtml(tc);
//			}
			else {
				event.replyError(this.getHelpReply());
			}
		}
	}

}
