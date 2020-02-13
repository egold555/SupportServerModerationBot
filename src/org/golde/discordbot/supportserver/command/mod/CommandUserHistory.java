package org.golde.discordbot.supportserver.command.mod;

import java.time.Instant;
import java.util.List;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.SimpleUser;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class CommandUserHistory extends ModCommand {

	public CommandUserHistory() {
		this.name = "userHistory";
		this.aliases = new String[] {"uh"};
		this.arguments = "<player>";
		this.help = "Shows you the players history of bans/kicks/mutes/warns";
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		//System.out.println(Arrays.toString(args.toArray(new String[0])));
		
		long thePerson = -1;
		
		if(args.size() == 0) {
			thePerson = event.getAuthor().getIdLong();
		}
		else if(args.size() == 2) {
			String msg = args.get(1);
			msg = msg.replace("<", "");
			msg = msg.replace("@", "");
			msg = msg.replace("!", "");
			msg = msg.replace(">", "");
			try {
				thePerson = Long.parseLong(msg);
			}
			catch(Exception e) {
				e.printStackTrace();
				event.replyError("Please use @<username> for the time being. I don't know how to search users by name yet -- Eric. Thanks for discovering this issue Si1kn!");
				/*Ignore*/
			}
		}
		else {
			event.replyError("Args must be 2");
		}
		
		if(thePerson != -1) {
			
			SimpleUser user = Database.getUser(thePerson);
			
			
			EmbedBuilder builder = new EmbedBuilder();
			//builder.setTitle(user.getUser().getUsername() + "'s Report");
			
			builder.addField("Bans", "" + user.getOffenceCount(ModAction.BAN), true);
			builder.addField("Kicks", "" + user.getOffenceCount(ModAction.KICK), true);
			builder.addBlankField(true);
			builder.addField("Warns", "" + user.getOffenceCount(ModAction.WARN), true);
			builder.addField("Mutes", "" + user.getOffenceCount(ModAction.MUTE), true);
			builder.addBlankField(true);
			
			builder.setTimestamp(Instant.now());
			builder.setFooter(Main.getJda().getSelfUser().getAsTag(), Main.getJda().getSelfUser().getAvatarUrl());
			
			builder.setAuthor(user.getUser().getUsername());
			if(user.getUser().getAvatar() == null || user.getUser().getAvatar().isEmpty() || user.getUser().getAvatar().equals("null")) {
				builder.setThumbnail("https://discordapp.com/assets/322c936a8c8be1b803cd94861bdfa868.png"); //default avatar
			}
			else {
				builder.setThumbnail(user.getUser().getAvatar());
			}
			
			event.getChannel().sendMessage(builder.build()).queue();;
			
			
		}
		else {
			event.replyError("User was not valid");
		}
		
	}

}
