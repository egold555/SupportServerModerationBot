package org.golde.discordbot.shared.util;

import java.awt.Color;

import org.golde.discordbot.shared.constants.SSEmojis;

public enum EnumReplyType {

	SUCCESS("**Success!**", Color.GREEN, SSEmojis.CHECK_MARK), 
	WARNING("**Warning!**", Color.YELLOW, SSEmojis.WARNING), 
	ERROR("**Error!**", Color.RED, SSEmojis.X), 
	NONE(" ", new Color(155, 89, 182), "");

	public final String title;
	public final Color color;
	public final String prefix;
	private EnumReplyType(String title, Color color, String prefix) {
		this.title = title;
		this.color = color;
		if(prefix != null) {
			this.prefix = prefix + " ";
		}
		else {
			this.prefix = "";
		}

	}
	
}
