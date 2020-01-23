package org.golde.discordbot.supportserver.command.mod;

import java.util.List;

import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.Roles;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class CommandUnmute extends ModCommand {

	public CommandUnmute() {
		this.guildOnly = true;
		this.name = "unmute";
		this.help = "unmute a player";
		this.arguments = "<player> [reason]";
		this.aliases = new String[]{"um"};
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		
		Member member = event.getMember();
		
		if(event.getArgs().isEmpty())
        {
            event.replyError("Please provide the name of a player to unmute!");
            return;
        }
		else {
		

	        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

	        if (args.isEmpty() || mentionedMembers.isEmpty()) {
	            event.replyError("Missing arguments");
	            return;
	        }

	        Member target = mentionedMembers.get(0);
	        String reason = String.join(" ", args.subList(2, args.size()));
	        
	        if(reason == null || reason.isEmpty()) {
	        	reason = "No reason provided.";
	        }
	        
	        Role mutedRole = event.getGuild().getRoleById(Roles.MUTE_ROLE);
	        
	        event.getGuild().removeRoleFromMember(target, mutedRole).queue();
	        
	        MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(ModLog.ModAction.UNMUTE, event.getAuthor(), target, reason);
	        ModLog.log(event.getGuild(), actionEmbed);
	        
	        target.getUser().openPrivateChannel().queue((dmChannel) ->
	        {
	        	dmChannel.sendMessage(actionEmbed).queue();
	        	
	        });

	        

	        event.replySuccess("Success!");
	        
			
		}
	}

}
