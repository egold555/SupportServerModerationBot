package org.golde.discordbot.supportserver.event;

import java.time.OffsetDateTime;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.shared.util.DateUtil;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class KidsWhoMakeAltAccountstoSpamAndBanEvadeHaveNoLife extends EventBase {

	public KidsWhoMakeAltAccountstoSpamAndBanEvadeHaveNoLife(ESSBot bot) {
		super(bot);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		
		User user = event.getUser();
		if(user.getTimeCreated().getMonth() == OffsetDateTime.now().getMonth() && user.getTimeCreated().getYear() == OffsetDateTime.now().getYear() && user.getTimeCreated().getDayOfWeek() == OffsetDateTime.now().getDayOfWeek()) {
			
			MuteManager.muteUser(bot, user.getIdLong(), bot.getJda().getSelfUser().getIdLong(), "Suspected spambot", DateUtil.parseDateDiff("1d", true));
			event.getGuild().getTextChannelById(Channels.Discussion.GENERAL_CHAT).sendMessage("<@199652118100049921> Auto muted potential spammer " + user.getAsMention() + ".").queue();;
		}
		
	}

}
