package org.golde.discordbot.supportserver.command.guildmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.constants.SSEmojis;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
	
	public CommandToggleRole(@Nonnull ESSBot bot) {
		super(bot, "toggleRole", "<" + RolesThatCanBeToggled.buildToggleableRoles() + "> <user>", "Toggle a persons roles", "tr");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		TextChannel tc = event.getTextChannel();
		Member member = event.getMember();
		Guild g = event.getGuild();
		
		if(event.getArgs().isEmpty())
        {
            replyError(tc, "Please provide the name of a player to toggle a role!");
            return;
        }
		else {
		
			Member target = getMember(event, args, 2);

			if (args.isEmpty() || target == null) {
				replyError(tc, "I could not find that person!");
				return;
			}
	       
	        
	        if (!member.canInteract(target)) {
	        	replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
	            return;
	        }
	        
	        try {
	        	RolesThatCanBeToggled rtcbt = RolesThatCanBeToggled.valueOf(args.get(1).toUpperCase());
	        	Role role = event.getGuild().getRoleById(rtcbt.id);
		        
	        	if(target.getRoles().contains(role)) {
	        		event.getGuild().removeRoleFromMember(target, role).queue();
	        		replySuccess(tc, "Successfully removed role " + role.getName() + " from " + target.getAsMention() + "!");
	        	}
	        	else {
	        		event.getGuild().addRoleToMember(target, role).queue();
	        		replySuccess(tc, "Successfully added role " + role.getName() + " to " + target.getAsMention() + "!");
	        	}
	        
	        }
	        catch(Exception e) {
	        	replyError(tc, "Whoops. That role was not found. Please use <" + RolesThatCanBeToggled.buildToggleableRoles() + ">");
	        }
	        
	        
	        
			
		}
		
	}

}
