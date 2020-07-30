package org.golde.discordbot.supportserver.database;

import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Offence {

	private ModAction action;
	private UserDataCache moderator;
	private String reason;
	private long timestamp;
	
	
	@Override
	public String toString() {
		return "Offence [action=" + action + ", moderator=" + moderator.getSnowflake() + ", reason=" + reason + ", timestamp="
				+ timestamp + "]";
	}
	
	
}
