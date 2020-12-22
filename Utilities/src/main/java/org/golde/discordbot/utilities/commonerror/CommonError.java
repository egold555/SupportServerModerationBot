package org.golde.discordbot.utilities.commonerror;

import lombok.Getter;

@Getter
public class CommonError {
	String[] shortCodes; //nullable
	String cmdDesc; //nullable
	String detailedDesc; //required
	String[] fileAttachments; //nullable
	String[] cmdArgs; //nullable
	String[] crashReport; //nullable
	String[] ocr; //nullable
	boolean fakeUser = false; //default false

	/**
	 * IDEA isn't liking the lombok here so I've added a method for it.
	 * @return uses a fake user.
	 */
	public boolean getFakeUser() {
		return fakeUser;
	}
}
