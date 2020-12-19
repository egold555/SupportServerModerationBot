package org.golde.discordbot.supportserver.event;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.event.AbstractMessageChecker;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class IPGrabberPrevention extends AbstractMessageChecker {

	public IPGrabberPrevention(ESSBot bot) {
		super(bot);
	}

	private static final List<String> BAD_URLS;
	
	static {
		BAD_URLS = FileUtil.readGenericConfig("ip-logger-urls");
	}


	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		String text = msg.getContentStripped();
		for(String bad : BAD_URLS) {
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

		//mute them
		Role mutedRole = guild.getRoleById(Roles.MUTED);

		guild.addRoleToMember(target, mutedRole).queue();

		Database.addOffence(bot, target.getIdLong(), guild.getSelfMember().getIdLong(), ModAction.MUTE, "IP Grabber Link");

		MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
				bot,
				ModAction.MUTE, 
				guild.getSelfMember().getUser(), 
				new String[][] {
					new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
					new String[] {"Reason:", "IP Grabber Link"}
				}
				);
		ModLog.log(guild, actionEmbed);

		target.getUser().openPrivateChannel().queue((dmChannel) ->
		{
			dmChannel.sendMessage(actionEmbed).queue();

		});



	}

}
