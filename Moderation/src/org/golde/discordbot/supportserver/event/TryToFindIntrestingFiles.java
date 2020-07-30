package org.golde.discordbot.supportserver.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.AbstractMessageChecker;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class TryToFindIntrestingFiles extends AbstractMessageChecker {

	public TryToFindIntrestingFiles(ESSBot bot) {
		super(bot);
	}

	final String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	@Override
	protected boolean checkMessage(Member sender, Message msg) {

		if(sender.getUser().isBot() || sender.getUser().isFake()) {
			return false;
		}

		if(OwnerCommand.isOwner(sender) || GuildModCommand.isModerator(sender) ) {
			return false;
		}

		String theMsg = msg.getContentStripped();
		Guild g = msg.getGuild();

		//System.out.println("Got: " + msg.getContentStripped() + " -- " + sender.getEffectiveName());


		Pattern p = Pattern.compile(REGEX,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(theMsg);


		boolean whatToReturn = false;

		if(m.find()) {

			//	    	//dont block channels on discord
			//	    	if(theMsg.contains("https://discordapp.com/channels/")) {
			//	    		return false;
			//	    	}
			//	    	
			//	    	if(theMsg.contains("https://youtube.com/")) {
			//	    		return false;
			//	    	}

			if(theMsg.contains(".exe")) {
				whatToReturn = true;
			}
			
			if(theMsg.contains("mediafire.com") || theMsg.contains("annonfile.com") || theMsg.contains("dropbox.com") || theMsg.contains("mega.nz") || theMsg.contains("mega.co.nz")) {
				intrestingFile(msg);
			}
		}


		if(!whatToReturn) {

			if(msg.getAttachments().size() != 0) {

				for(Attachment att : msg.getAttachments()) {
					
					if(att.getFileExtension().equalsIgnoreCase("exe")) {
						whatToReturn = true;
						
						//g.getTextChannelById(Channels.MOD_LOGS).sendMessage("EXE file sent. Should we check this?  " + msg.getJumpUrl()).queue();
					}

					if(att.getFileExtension().equalsIgnoreCase("zip")) {
						intrestingFile(msg);
					}

					if(att.getFileExtension().equalsIgnoreCase("rar")) {
						intrestingFile(msg);
					}

				}

			}

		}


		return whatToReturn;
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		
		replyError(msg.getChannel(), "Links / Files like this are current not allowed to be sent. Please contact a moderator for further assistance.", 10);
		
		intrestingFile(msg);
		
		msg.delete().queue();
	}

	private void intrestingFile(Message msg) {
		Guild guild = msg.getGuild();
		Member author = msg.getMember();
		String attachments = "";
		for(Attachment a : msg.getAttachments()) {
			attachments += a.getFileName() + " - " + a.getUrl() + "\n";
		}
		
		String desc = "**Author:** " + author.getEffectiveName() + "\n**Message Link:**" + msg.getJumpUrl() + "\n";
		
		
		if(msg.getContentRaw().length() > 0) {
			desc += "**Message:**" + msg.getContentDisplay() + "\n";
		}
		
		if(!attachments.isEmpty() && attachments.length() > 1) {
			desc += "**Attachments**: \n" + attachments;
		}
		
		reply(guild.getTextChannelById(Channels.MOD_LOGS), "Intresting file/link detected", desc);
	}



}
