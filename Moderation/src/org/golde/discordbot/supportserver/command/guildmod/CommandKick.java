package org.golde.discordbot.supportserver.command.guildmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.constants.MiscConstants;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandKick extends GuildModCommand {

	public CommandKick(@Nonnull ESSBot bot) {
		super(bot, "kick", "<player> [reason]", "kick a player", "k");
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
		
			Member target = getMember(event, args, 1);

			if (args.isEmpty() || target == null) {
				replyError(tc, "I could not find that person!");
				return;
			}

	        String reason = String.join(" ", args.subList(2, args.size()));

	        if (!event.getMember().canInteract(target) || target.getUser().isBot() || target.getUser().isFake()) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}
	        

	        if(reason == null || reason.isEmpty()) {
	        	reason = "No reason provided.";
	        }
	        
	        final String reasonFinal = reason;
	        
	        Long offenceId = Offence.addOffence(bot, new Offence(target.getIdLong(), event.getAuthor().getIdLong(), ModAction.KICK, reason));
	        replySuccess(tc, "Entry added with id: " + offenceId);
	       // Database.addOffence(bot, target.getIdLong(), event.getAuthor().getIdLong(), ModAction.KICK, reason);
	        
	        MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
	        		bot,
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
			        	
			        });
		        });
	        	
	        });
	        
	        event.getGuild().kick(target, String.format("Kick by: %#s, with reason: %s",
	                event.getAuthor(), reasonFinal)).queue();

	        

	        replySuccess(tc, "Successfully kicked " + target.getAsMention() + " for '**" + StringUtils.abbreviate(reason, 250) + "**'!");

	        
			
		}
	}

}
