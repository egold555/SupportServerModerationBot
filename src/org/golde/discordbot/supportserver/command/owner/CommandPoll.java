package org.golde.discordbot.supportserver.command.owner;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.supportserver.util.SuggestionImageGenerator;
import org.golde.discordbot.supportserver.util.SuggestionImageGenerator.Poll;
import org.golde.discordbot.supportserver.util.SuggestionImageGenerator.Suggestion;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandPoll extends OwnerCommand {

	static int ID_POLL = 0;
	static int ID_SUGGESTIONS = 0;
	static List<Poll> TOTAL_POLLS = new ArrayList<Poll>();
	
	public CommandPoll() {
		super("poll", "<create | addSuggestion | update>", "Does poll stuff");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		if(args.size() == 0) {
			event.replyError(this.PREFIX + this.name + " " + this.arguments);
			return;
		}
		event.getMessage().delete().queue();
		String subcmd = args.get(1).toLowerCase();
		if(subcmd.equals("create")) {
			//create a poll
			Poll p = new Poll(ID_POLL, String.join(" ", args.subList(2, args.size())));
			ID_POLL++;
			p.sendImage(event.getTextChannel());
			TOTAL_POLLS.add(p);
		}
		else if(subcmd.equals("addsuggestion")) {
			int idToFind;
			try {
				idToFind = Integer.parseInt(args.get(2));
			}
			catch(NumberFormatException e) {
				event.replyError("**" + args.get(2) + "** is not a valid number!");
				return;
			}
			
			Poll found = null;
			for(Poll p : TOTAL_POLLS) {
				if(p.getId() == idToFind) {
					found = p;
				}
			}
			
			if(found == null) {
				event.replyError("Poll with the id **" + idToFind + "** was not found.");
				return;
			}
			
			String title = String.join(" ", args.subList(3, args.size()));
			if(title == null || title.length() < 5) {
				event.replyError("Title must be more then 5 characters");
				return;
			}
			
			found.addSuggestion(new Suggestion(ID_SUGGESTIONS, title));
			event.replySuccess("success.");
			found.sendImage(event.getTextChannel());
			
			ID_SUGGESTIONS++;
		}
		else if(subcmd.equals("removesuggestion")) {
			int idToFind;
			try {
				idToFind = Integer.parseInt(args.get(2));
			}
			catch(NumberFormatException e) {
				event.replyError("**" + args.get(2) + "** is not a valid number!");
				return;
			}
			
			Poll pollfound = null;
			Suggestion suggestionfound = null;
			for(Poll p : TOTAL_POLLS) {
				for(Suggestion s : p.getSuggestions()) {
					if(s.getId() == idToFind) {
						pollfound = p;
						suggestionfound = s;
					}
				}
			}
			
			if(suggestionfound == null) {
				event.replyError("Suggestion with the id **" + idToFind + "** was not found.");
				return;
			}
			
			pollfound.removeSuggestion(suggestionfound);
			event.replySuccess("success.");
			pollfound.sendImage(event.getTextChannel());
		}
		else if(subcmd.equals("update")) {
			int idToFind;
			try {
				idToFind = Integer.parseInt(args.get(2));
			}
			catch(NumberFormatException e) {
				event.replyError("**" + args.get(2) + "** is not a valid number!");
				return;
			}
			
			Poll found = null;
			for(Poll p : TOTAL_POLLS) {
				if(p.getId() == idToFind) {
					found = p;
				}
			}
			
			if(found == null) {
				event.replyError("Poll with the id **" + idToFind + "** was not found.");
				return;
			}
			
			found.sendImage(event.getTextChannel());
		}
	}

}
