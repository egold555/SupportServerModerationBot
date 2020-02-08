package org.golde.discordbot.supportserver.database;

import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Offence {

	private ModAction action;
	private UsernameCache moderator;
	private String reason;
	private long timestamp;
	
	
}