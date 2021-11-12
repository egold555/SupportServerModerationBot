package org.golde.discordbot.supportserver.command.chatmod;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandUserHistory extends ChatModCommand {

	public CommandUserHistory(@Nonnull ESSBot bot) {
		super(bot, "userHistory", "<player> [BAN | WARN | MUTE | KICK | ALL]", "Shows you the players history of bans/kicks/mutes/warns", "uh");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {


		Guild g = event.getGuild();
		TextChannel tc = event.getTextChannel();
		Long targetId = getMember(event, args, 1);

		
		if(args.isEmpty() || targetId == null) {
			replyError(tc, getHelpReply());
			return;
		}
		
		Member target = event.getGuild().getMemberById(targetId);


		if(target == null) {
			replyWarning(tc, "I could not find this player on the guild, but will attempt to preform the given action anyway...");
			
			bot.getJda().retrieveUserById(targetId).queue(
					newUser -> {
						doThing(event, args, g, tc, targetId, newUser);
					}, 
					fail -> {
						doThing(event, args, g, tc, targetId, null);
					});
			
		}
		else {
			doThing(event, args, g, tc, targetId, target.getUser());
		}

		


	}
	
	void doThing(CommandEvent event, List<String> args, Guild g, TextChannel tc, Long targetId, User user) {
		if(args.size() == 2) {
			EmbedBuilder builder = new EmbedBuilder();

			builder.addField("Bans", Integer.toString(Offence.getTotalOffencesByCategory(bot, targetId, ModAction.BAN).size()), true);
			builder.addField("Kicks", Integer.toString(Offence.getTotalOffencesByCategory(bot, targetId, ModAction.KICK).size()), true);
			builder.addBlankField(true);
			builder.addField("Warns", Integer.toString(Offence.getTotalOffencesByCategory(bot, targetId, ModAction.WARN).size()), true);
			builder.addField("Mutes", Integer.toString(Offence.getTotalOffencesByCategory(bot, targetId, ModAction.MUTE).size()), true);
			builder.addBlankField(true);

			builder.setTimestamp(Instant.now());
			builder.setFooter(bot.getJda().getSelfUser().getAsTag(), bot.getJda().getSelfUser().getAvatarUrl());

			if(user != null) {
				builder.setAuthor(user.getAsTag());
				builder.setThumbnail(user.getEffectiveAvatarUrl());
			}
			else {

				

				//default avatar if we could not find it. Maybe we save this in the database?
				builder.setAuthor(targetId.toString());
				builder.setThumbnail("https://cdn.discordapp.com/embed/avatars/3.png");
			}


			event.getChannel().sendMessage(builder.build()).queue();;
		}
		else {
			StringBuilder csv = new StringBuilder();

			csv.append("Date Issued");
			csv.append(',');

			csv.append("Date Expired");
			csv.append(',');

			csv.append("Moderator");
			csv.append(',');

			csv.append("Offence Type");
			csv.append(',');

			csv.append("Reason");
			csv.append("\n");

			String specific = args.get(2).toUpperCase().trim();

			if(specific.equals("ALL")) {

				for(Offence o : Offence.getAllOffences(bot, targetId)) {
					csv.append(o.getAsCSVLine(g));
				}

			}
			else {
				ModAction action = null;
				try {
					action = ModAction.valueOf(specific.trim());
				}
				catch(Exception e) {
					replyError(tc, "Unknown specific: " + specific);
					return;
				}

				for(Offence o : Offence.getTotalOffencesByCategory(bot, targetId, action)) {
					csv.append(o.getAsCSVLine(g));
				}

			}

			tc.sendFile(csv.toString().getBytes(), targetId + ".csv").queue();;
			replySuccess(tc, "Generated a CSV file for the following infractions: " + specific + ".");

		}
	}

}
