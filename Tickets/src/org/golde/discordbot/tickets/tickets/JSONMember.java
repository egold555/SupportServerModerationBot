package org.golde.discordbot.tickets.tickets;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

@Getter
public class JSONMember {

	private final String name;
	private final String avatar;
	
	public JSONMember(Member in) {
		if(in == null) {
			name = "null";
			avatar = "null";
			return;
		}
		name = in.getEffectiveName();
		avatar = in.getUser().getEffectiveAvatarUrl();
	}
	
}
