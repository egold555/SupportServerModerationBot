package org.golde.discordbot.supportserver.command.guildmod;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.supportserver.constants.MiscConstants;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandKick extends GuildModCommand {

	public CommandKick() {
		super("kick", "<player> [reason]", "kick a player", "k");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		TextChannel tc = event.getTextChannel();
		
		if(event.getArgs().isEmpty())
        {
            replyError(tc, "Please provide the name of a player to kick!");
            return;
        }
		else {
		
	        Member selfMember = event.getMember(); //Whoops.
	        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

	        if (args.isEmpty() || mentionedMembers.isEmpty()) {
	            replyError(tc, "Missing arguments");
	            return;
	        }

	        Member target = mentionedMembers.get(0);
	        String reason = String.join(" ", args.subList(2, args.size()));

	        if (!selfMember.hasPermission(Permission.KICK_MEMBERS) || !selfMember.canInteract(target) || selfMember.equals(target)) {
	        	replyError(tc, "I'm sorry ~~Dave~~ " + event.getMember().getAsMention() + ", I'm afraid I can't do that" );
	            return;
	        }
	        
//	        if(!target.canInteract(target)) {
//				replyError(tc, "Sorry you can not interact with that user! Please contact Eric.");
//				return;
//			}
	        
	        if(reason == null || reason.isEmpty()) {
	        	reason = "No reason provided.";
	        }
	        
	        final String reasonFinal = reason;
	        
	        Database.addOffence(target.getIdLong(), event.getAuthor().getIdLong(), ModAction.KICK, reason);
	        
	        MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					ModAction.KICK, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Reason:", StringUtils.abbreviate(reason, 250)}
					}
					);
	        ModLog.log(event.getGuild(), actionEmbed);
	        
	        //TODO: Add discord link back to the server
	        target.getUser().openPrivateChannel().queue((dmChannel) ->
	        {
	        	dmChannel.sendMessage(actionEmbed).queue((unused1) ->
		        {
		        	dmChannel.sendMessage("You can join back with this link: " + MiscConstants.DISCORD_INVITE).queue((unused2) ->
			        {
			        	event.getGuild().kick(target, String.format("Kick by: %#s, with reason: %s",
				                event.getAuthor(), reasonFinal)).queue();
			        });
		        });
	        	
	        });

	        

	        replySuccess(tc, "Success!");
	        
			
		}
	}

}
