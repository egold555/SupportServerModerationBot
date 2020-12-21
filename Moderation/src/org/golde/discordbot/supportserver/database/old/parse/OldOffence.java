package org.golde.discordbot.supportserver.database.old.parse;

import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Deprecated
public class OldOffence {

	private ModAction action;
	private UserDataCache moderator;
	private String reason;
	private long timestamp;
	
	
	
	@Override
	public String toString() {
		return "Offence [action=" + action + ", moderator=" + moderator.getSnowflake() + ", reason=" + reason + ", timestamp="
				+ timestamp + "]";
	}
	
	public Offence translate(UserDataCache offender) {
		return new Offence(offender.getSnowflake(), moderator.getSnowflake(), action, reason);
	}
	
	
}