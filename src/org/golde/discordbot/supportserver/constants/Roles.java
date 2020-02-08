package org.golde.discordbot.supportserver.constants;

import org.golde.discordbot.supportserver.Main;

import net.dv8tion.jda.api.entities.Role;

public enum Roles {

	MUTED("638114097359093765", "\u1F507 Muted"),
	EVERYONE("594335572173258752", "@everyone");
	
	private final String id;
	private final String roleName;
	Roles(String id, String roleName){
		this.id = id;
		this.roleName = roleName;
	}
	
	public String getId() {
		return id;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public Role getRole() {
		return Main.getGuild().getRoleById(id);
	}
	
}
