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
}
