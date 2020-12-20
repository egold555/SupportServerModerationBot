package org.golde.discordbot.supportserver.event;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.shared.db.ICanHasDatabaseFile;
import org.golde.discordbot.shared.event.AbstractMessageChecker;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.DateUtil;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MuteManager extends AbstractMessageChecker implements ICanHasDatabaseFile {

	public MuteManager(ESSBot bot) {
		super(bot);
	}

	private static List<String> BAD_WORDS;

	static {
		BAD_WORDS = FileUtil.readGenericConfig("not-ok-words");
	}

	@Override
	public void reload() {
		BAD_WORDS.clear();
		BAD_WORDS = null;
		BAD_WORDS = FileUtil.readGenericConfig("not-ok-words");
	}


	@Override
	protected boolean checkMessage(Member sender, Message msg) {

		if(sender.isOwner() || sender.isFake() || sender.getUser().isBot()) {
			return false;
		}

		String text = msg.getContentStripped();
		for(String bad : BAD_WORDS) {
			if(text.toLowerCase().contains(bad.toLowerCase())) {
				return true;
			}
		}

		return false;

	}

	//target is null if its a webhook
	//Fix
	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {

		//delete their message
		msg.delete().queue();

		muteUser(bot, target.getIdLong(), bot.getJda().getSelfUser().getIdLong(), "[Auto Mute] Offensive Language.", DateUtil.parseDateDiff("1d", true));

	}

	//stop mute perms from becoming broken :|
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		super.onGuildMessageReceived(event);

		if(event.getAuthor().isBot() || event.getAuthor().isFake()) {
			return;
		}

		if(event.getMember().getRoles().contains(event.getGuild().getRoleById(Roles.MUTED))) {
			if(event.getChannel().getIdLong() != Channels.MiscellaneousChats.REDEEM_YOUR_MUTE) {
				event.getMessage().delete().queue();
				replyError(event.getChannel(), "You are muted, you can not speak.", 2);
			}
		}
	}

	public static MessageEmbed muteUser(ESSBot bot, long offender, long moderator, String reason, Long expireTime) {
		Guild g = bot.getGuild();

		MessageEmbed embed = doMuteUnmuteDBEntry(bot, offender, moderator, ModAction.MUTE, reason, expireTime);

		Role mutedRole = g.getRoleById(Roles.MUTED);

		g.getTextChannelById(Channels.MiscellaneousChats.REDEEM_YOUR_MUTE).sendMessage(embed).queue();

		g.addRoleToMember(offender, mutedRole).queue();

		return embed;
	}

	public static MessageEmbed unmuteUser(ESSBot bot, long offender, long moderator, String reason) {
		Guild g = bot.getGuild();

		MessageEmbed embed = doMuteUnmuteDBEntry(bot, offender, moderator, ModAction.UNMUTE, reason, null);

		Role mutedRole = g.getRoleById(Roles.MUTED);

		g.removeRoleFromMember(offender, mutedRole).queue();

		return embed;
	}

	private static MessageEmbed doMuteUnmuteDBEntry(ESSBot bot, long offender, long moderator, ModAction action, String reason, Long expireTime) {
		Guild g = bot.getGuild();
		Long offenceId = Offence.addOffence(bot, new Offence(offender, moderator, ModAction.MUTE, reason, expireTime));

		MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
				bot,
				ModAction.MUTE, 
				g.getMemberById(moderator).getUser(), 
				new String[][] {
					new String[] {"Offender: ", "<@" + offender + ">"}, 
					new String[] {"Reason:", StringUtils.abbreviate(reason, 250)},
					new String[] {"Expires:", expireTime != null ? StringUtils.abbreviate(DateUtil.formatDateDiff(expireTime), 250) : "Not specified"},
					new String[] {"Offence ID:", Long.toString(offenceId)},
				}
				);

		ModLog.log(g, actionEmbed);
		tryToDmUser(g.getMemberById(offender), actionEmbed, null);
		return actionEmbed;
	}

}
