package org.golde.discordbot.supportserver.command.guildmod;

import java.time.Instant;
import java.util.List;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.SimpleUser;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandUserHistory extends GuildModCommand {

	public CommandUserHistory() {
		super("userHistory", "<player>", "Shows you the players history of bans/kicks/mutes/warns", "uh");
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		//System.out.println(Arrays.toString(args.toArray(new String[0])));
		
		long thePerson = -1;
		Guild g = event.getGuild();
		TextChannel tc = event.getTextChannel();
		
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
				replyError(tc, "Please use @<username> for the time being. I don't know how to search users by name yet -- Eric. Thanks for discovering this issue Si1kn!");
				/*Ignore*/
			}
		}
		else {
			replyError(tc, "Args must be 2");
		}
		
		if(thePerson != -1) {
			
			SimpleUser user = Database.getUser(thePerson);
			Member userMember = g.getMemberById(thePerson);
			
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
			
			if(userMember != null) {
				builder.setAuthor(userMember.getUser().getAsTag());
				builder.setThumbnail(userMember.getUser().getEffectiveAvatarUrl());
			}
			else {
				builder.setAuthor(thePerson + "");
				builder.setThumbnail("https://discordapp.com/assets/322c936a8c8be1b803cd94861bdfa868.png"); //default avatar
			}
			
			event.getChannel().sendMessage(builder.build()).queue();;
			
			
		}
		else {
			replyError(tc, "User was not valid");
		}
		
	}

}
