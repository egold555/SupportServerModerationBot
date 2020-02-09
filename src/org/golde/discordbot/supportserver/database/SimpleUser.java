package org.golde.discordbot.supportserver.database;

import java.util.ArrayList;

import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import lombok.Getter;

@Getter
public class SimpleUser {

	private final UserDataCache user;
	
	private ArrayList<Offence> offences = new ArrayList<Offence>();
	
	public SimpleUser(UserDataCache user) {
		this.user = user;
	}
	
	public int getAmountOfWarns() {
		return getOffenceCount(ModAction.WARN);
	}
	
	public int getAmountOfMutes() {
		return getOffenceCount(ModAction.MUTE);
	}
	
	public int getAmountOfOffences() {
		return offences.size();
	}
	
	public void addOffence(Offence o) {
		offences.add(o);
	}
	
	public int getOffenceCount(ModAction action) {
		int toReturn = 0;
		
		for(Offence o : offences) {
			if(o.getAction() == action) {
				toReturn++;
			}
		}
		
		return toReturn;
	}

	@Override
	public String toString() {
		return "SimpleUser [user=" + user + ", offences=" + offences + ", getAmountOfWarns()=" + getAmountOfWarns()
				+ ", getAmountOfMutes()=" + getAmountOfMutes() + ", getAmountOfOffences()=" + getAmountOfOffences()
				+ "]";
	}
	
	
	
}
