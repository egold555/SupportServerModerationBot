package org.golde.discordbot.supportserver.command.guildmod;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.supportserver.constants.Roles;
import org.golde.discordbot.supportserver.constants.SSEmojis;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandToggleRole extends GuildModCommand {

	private enum RolesThatCanBeToggled {
		
		CONTRIBUTOR(Roles.CONTRIBUTOR), 
		CODEHELPER(Roles.CODE_HELPER), 
		BIRTHDAY(Roles.ITS_MY_B_DAY)
		;
		
		private final long id;
		RolesThatCanBeToggled(long id){
			this.id = id;
		}
		
		static String buildToggleableRoles() {
			
			StringBuilder builder = new StringBuilder();
			
			for(int i = 0; i < values().length; i++) {
				RolesThatCanBeToggled rtcbt = values()[i];
				builder.append(rtcbt.name());
				if(i != (values().length - 1)) {
					builder.append(", ");
				}
			}
			return builder.toString();
		}
	}
	
	public CommandToggleRole() {
		super("toggleRole", "<" + RolesThatCanBeToggled.buildToggleableRoles() + "> <user>", "Toggle a persons roles", "tr");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		TextChannel tc = event.getTextChannel();
		Member member = event.getMember();
		Guild g = event.getGuild();
		
		if(event.getArgs().isEmpty())
        {
            replyError(tc, "Please provide the name of a player to mute!");
            return;
        }
		else {
		
	        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

	        if (args.isEmpty() || mentionedMembers.isEmpty()) {
	            replyError(tc, "Missing arguments");
	            return;
	        }

	        Member target = mentionedMembers.get(0);
	       
	        
	        if (!member.canInteract(target)) {
	        	replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
	            return;
	        }
	        
	        try {
	        	RolesThatCanBeToggled rtcbt = RolesThatCanBeToggled.valueOf(args.get(1).toUpperCase());
	        	Role role = event.getGuild().getRoleById(rtcbt.id);
		        
	        	if(target.getRoles().contains(role)) {
	        		event.getGuild().removeRoleFromMember(target, role).queue();
	        	}
	        	else {
	        		event.getGuild().addRoleToMember(target, role).queue();
	        	}
	        	
		        replySuccess(tc, "Success!");
	        }
	        catch(Exception e) {
	        	replyError(tc, "Whoops. That role was not found. Please use <" + RolesThatCanBeToggled.buildToggleableRoles() + ">");
	        }
	        
	        
	        
			
		}
		
	}

}
