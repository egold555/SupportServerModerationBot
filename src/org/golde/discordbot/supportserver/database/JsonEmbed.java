package org.golde.discordbot.supportserver.database;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JsonEmbed {

	private String action;
	private String moderator;
	private String offender;
	private String reason;
	private long timestamp;
	
}
