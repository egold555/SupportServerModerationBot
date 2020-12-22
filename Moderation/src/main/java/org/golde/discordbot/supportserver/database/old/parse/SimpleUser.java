package org.golde.discordbot.supportserver.database.old.parse;

import java.util.ArrayList;
import java.util.Iterator;

import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import lombok.Getter;

@Getter
@Deprecated
public class SimpleUser {

	private final UserDataCache user;
	
	private ArrayList<OldOffence> offences = new ArrayList<OldOffence>();
	
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
	
	public void addOffence(OldOffence o) {
		offences.add(o);
	}
	
	public int getOffenceCount(ModAction action) {
		int toReturn = 0;
		
		for(OldOffence o : offences) {
			if(o.getAction() == action) {
				toReturn++;
			}
		}
		
		return toReturn;
	}
	
	public void removeLastOffence(ModAction action) {
		Iterator<OldOffence> it = offences.iterator();
		while(it.hasNext()) {
			if(it.next().getAction() == action) {
				it.remove();
				return;
			}
		}
	}

	@Override
	public String toString() {
		return "SimpleUser [user=" + user + ", offences=" + offences + ", getAmountOfWarns()=" + getAmountOfWarns()
				+ ", getAmountOfMutes()=" + getAmountOfMutes() + ", getAmountOfOffences()=" + getAmountOfOffences()
				+ "]";
	}
	
	
	
}