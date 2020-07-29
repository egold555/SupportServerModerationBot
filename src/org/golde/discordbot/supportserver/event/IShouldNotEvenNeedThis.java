package org.golde.discordbot.supportserver.event;

import java.util.Set;

import org.golde.discordbot.supportserver.constants.Roles;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.FileUtil;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class IShouldNotEvenNeedThis extends AbstractMessageChecker {

	private static final Set<String> BAD_WORDS;
	
	static {
		BAD_WORDS = FileUtil.readGenericConfig("not-ok-words");
	}


	@Override
	protected boolean checkMessage(Member sender, Message msg) {
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

		//mute them
		Role mutedRole = guild.getRoleById(Roles.MUTED);

		guild.addRoleToMember(target, mutedRole).queue();

		Database.addOffence(target.getIdLong(), guild.getSelfMember().getIdLong(), ModAction.MUTE, "Very offensive language.");

		MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
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
