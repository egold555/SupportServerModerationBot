package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.supportserver.database.Database;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DatabaseEventListener extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		
		Member member = event.getMember();
		
		Database.updateUsername(member.getIdLong(), member.getUser().getAsTag());
		
	}
	
	@Override
	public void onUserUpdateName(UserUpdateNameEvent event) {
		User member = event.getEntity();
		Database.updateUsername(member.getIdLong(), member.getAsTag());
	}
	
}
