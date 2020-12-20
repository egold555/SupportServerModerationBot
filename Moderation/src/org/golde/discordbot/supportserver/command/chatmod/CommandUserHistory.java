package org.golde.discordbot.supportserver.command.chatmod;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandUserHistory extends ChatModCommand {

	public CommandUserHistory(@Nonnull ESSBot bot) {
		super(bot, "userHistory", "<player> [specific | ALL]", "Shows you the players history of bans/kicks/mutes/warns", "uh");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {


		Guild g = event.getGuild();
		TextChannel tc = event.getTextChannel();
		Member target = getMember(event, args, 1);
		Long targetId = null;

		if (args.isEmpty() || target == null) {
			try {
				targetId = Long.parseLong(args.get(1));
			}
			catch(NumberFormatException e) {
				replyError(tc, "Invalid ID format specified!");
				return;
			}
		}

		if(target != null)  {
			targetId = target.getIdLong();
		}

		if(targetId == null) {
			replyError(tc, "Invalid person specified. They were not on the server, and when attempted to get the userId I got: " + args.get(1));
			return;
		}

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

			if(target != null) {
				builder.setAuthor(target.getUser().getAsTag());
				builder.setThumbnail(target.getUser().getEffectiveAvatarUrl());
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
