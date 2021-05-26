package org.golde.discordbot.supportserver.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.shared.util.EnumReplyType;

import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

//sad that is has come to this
public class EventLetUsersKnowThatWeCanOnlyHelpWithEricsTutorialVideos extends EventBase {

	//author, list of titles
	
	//Should be a DB of some sort, but its late.
	//Maybe ill fix this, or just forget about it down the line.
	static HashMap<String, List<String>> map = new HashMap<String, List<String>>();
	static {
		map.put("quickdaffy", Arrays.asList(new String[] {"how to code a minecraft pvp client"}));
		map.put("EnergixCoding", Arrays.asList(new String[] {"how to make a minecraft pvp client"}));
		map.put("hqcc", Arrays.asList(new String[] {"how to make a minecraft pvp client"}));
		map.put("PhoneusPVP", Arrays.asList(new String[] {"minecraft 1.8.8 pvp client coding tutorial"}));
		map.put("MrBlackReal", Arrays.asList(new String[] {"minecraft pvp client"}));
		map.put("FELIX", Arrays.asList(new String[] {"eigenen minecraft pvp client coden", "minecraft pvp client"}));
	}
	
	public EventLetUsersKnowThatWeCanOnlyHelpWithEricsTutorialVideos(ESSBot bot) {
		super(bot);
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
//		if(event.getChannel().getIdLong() != Channels.BotDebugging.COMMAND_TESTING) {
//			return;
//		}
		
		if(event.getAuthor().isBot()) {
			return;
		}
		
		List<MessageEmbed> embeds = event.getMessage().getEmbeds();
		for(MessageEmbed embed : embeds) {
			if(embed.getType() == EmbedType.VIDEO) {
				if(embed.getUrl().startsWith("https://www.youtube.com/watch?v=")) {
					String author = embed.getAuthor().getName();
					String title = embed.getTitle().toLowerCase();
					List<String> titles = map.get(author.toLowerCase());
					if(titles != null) {
						//System.out.println("no null");
						for(String tit : titles) {
							//System.out.println(tit + " - " + title);
							if(title.contains(tit)) {
								String replyTitle = "This is not one of Eric's Tutorials!";
								String replyDesc = "It looks like you posted a PVP client tutorial by " + author + ". "
										+ "\n\nIt may be hard for us to help you because this is not one of Eric's tutorials."
										+ "\n\nWe recomend that you go to " + author + "'s support server for help with their tutorials.";
								event.getMessage().reply(this.getReplyEmbed(EnumReplyType.WARNING, replyTitle, replyDesc)).queue();;
								
							}
						}
					}
				}
				
			}
		}
		
		super.onGuildMessageReceived(event);
	}

}
