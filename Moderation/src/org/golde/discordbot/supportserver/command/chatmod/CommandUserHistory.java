package org.golde.discordbot.supportserver.command.chatmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandUserHistory extends ChatModCommand {

	public CommandUserHistory(@Nonnull ESSBot bot) {
		super(bot, "disabled-userHistory", "<player>", "Shows you the players history of bans/kicks/mutes/warns", "uh");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
//
//
//		Guild g = event.getGuild();
//		TextChannel tc = event.getTextChannel();
//		Member target = getMember(event, args, 1);
//
//		if (args.isEmpty() || target == null) {
//			replyError(tc, "I could not find that person!");
//			return;
//		}
		
		throw new NotImplementedException("Not implemented!");

//		SimpleUser user = Database.getUser(target.getIdLong());
//
//
//		EmbedBuilder builder = new EmbedBuilder();
//
//		builder.addField("Bans", "" + user.getOffenceCount(ModAction.BAN), true);
//		builder.addField("Kicks", "" + user.getOffenceCount(ModAction.KICK), true);
//		builder.addBlankField(true);
//		builder.addField("Warns", "" + user.getOffenceCount(ModAction.WARN), true);
//		builder.addField("Mutes", "" + user.getOffenceCount(ModAction.MUTE), true);
//		builder.addBlankField(true);
//
//		builder.setTimestamp(Instant.now());
//		builder.setFooter(bot.getJda().getSelfUser().getAsTag(), bot.getJda().getSelfUser().getAvatarUrl());
//
//		builder.setAuthor(target.getUser().getAsTag());
//		builder.setThumbnail(target.getUser().getEffectiveAvatarUrl());
//
//
//		event.getChannel().sendMessage(builder.build()).queue();;




	}

}
