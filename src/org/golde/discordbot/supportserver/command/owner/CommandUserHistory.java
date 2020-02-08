package org.golde.discordbot.supportserver.command.owner;

import java.util.Arrays;
import java.util.List;

import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.SimpleUser;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandUserHistory extends OwnerCommand {

	public CommandUserHistory() {
		this.name = "userHistory";
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		System.out.println(Arrays.toString(args.toArray(new String[0])));
		
		long thePerson = -1;
		
		if(args.size() == 0) {
			thePerson = event.getAuthor().getIdLong();
		}
		else if(args.size() == 2) {
			String msg = args.get(1);
			msg = msg.replace("<@", "");
			msg = msg.replace(">", "");
			try {
				thePerson = Long.parseLong(msg);
			}
			catch(Exception e) {
				/*Ignore*/
			}
		}
		else {
			event.replyError("Args must be 2");
		}
		
		if(thePerson != -1) {
			
			SimpleUser user = Database.getUser(thePerson);
			
			System.out.println(user.toString());
			event.replySuccess("Check console");
			
		}
		else {
			event.replyError("User was not valid");
		}
		
	}

}
