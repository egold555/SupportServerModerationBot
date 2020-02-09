package org.golde.discordbot.supportserver.command.owner;

import java.time.Instant;
import java.util.List;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.SimpleUser;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class CommandUserHistory extends OwnerCommand {

	public CommandUserHistory() {
		this.name = "userHistory";
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
				/*Ignore*/
			}
		}
		else {
			event.replyError("Args must be 2");
		}
		
		if(thePerson != -1) {
			
			SimpleUser user = Database.getUser(thePerson);
			
//			String json = Database.GSON.toJson(user);
//			
//			event.getChannel().sendFile(json.getBytes(), user.getUser().getSnowflake() + ".json").append("File sent").queue();;
//			
//			event.replySuccess("Check console");
			
			
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
			
			Member mem = Main.getGuild().getMemberById(thePerson);
			if(mem != null) {
				
				System.out.println(mem.getUser().getAvatarUrl());
				builder.setAuthor(mem.getUser().getAsTag());
				builder.setThumbnail(mem.getUser().getAvatarUrl());
			}
			
			
			
			event.getChannel().sendMessage(builder.build()).queue();;
			
			
		}
		else {
			event.replyError("User was not valid");
		}
		
	}

}
