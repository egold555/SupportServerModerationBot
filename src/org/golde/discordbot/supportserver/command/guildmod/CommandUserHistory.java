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


		Guild g = event.getGuild();
		TextChannel tc = event.getTextChannel();
		Member target = getMember(event, args, 1);

		if (args.isEmpty() || target == null) {
			replyError(tc, "Missing or invalid arguments");
			return;
		}

		SimpleUser user = Database.getUser(target.getIdLong());


		EmbedBuilder builder = new EmbedBuilder();

		builder.addField("Bans", "" + user.getOffenceCount(ModAction.BAN), true);
		builder.addField("Kicks", "" + user.getOffenceCount(ModAction.KICK), true);
		builder.addBlankField(true);
		builder.addField("Warns", "" + user.getOffenceCount(ModAction.WARN), true);
		builder.addField("Mutes", "" + user.getOffenceCount(ModAction.MUTE), true);
		builder.addBlankField(true);

		builder.setTimestamp(Instant.now());
		builder.setFooter(Main.getJda().getSelfUser().getAsTag(), Main.getJda().getSelfUser().getAvatarUrl());

		builder.setAuthor(target.getUser().getAsTag());
		builder.setThumbnail(target.getUser().getEffectiveAvatarUrl());


		event.getChannel().sendMessage(builder.build()).queue();;




	}

}
