package org.golde.discordbot.utilities.event;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.shared.db.ICanHasDatabaseFile;
import org.golde.discordbot.shared.event.AbstractMessageChecker;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class LikeDislikePollEvents extends AbstractMessageChecker implements ICanHasDatabaseFile {

	private static List<Long> IDS = new ArrayList<Long>();
	
	@Override
	public void reload() {
		IDS.clear();
		IDS = FileUtil.loadArrayFromFile("like-dislike-poll-channels", Long[].class);
		//System.out.println(IDS);
	}
	
	public LikeDislikePollEvents(ESSBot bot) {
		super(bot);
	}

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		
		if(!IDS.contains(msg.getTextChannel().getIdLong())) {
			return false;
		}
		
		if(sender.isFake() || sender.getUser().isBot()) {
			return false;
		}
		
		if(sender.isOwner() && msg.getAttachments().size() == 0) {
			return false;
		}
		
		if(msg.getAttachments().size() == 0) {
			msg.delete().queue();
			replyError(msg.getChannel(), sender.getAsMention() + ", You must provide a image to vote on!", 10);
			return false;
		}
		
		return true;
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		
		msg.addReaction("like:604876349844226060").queue(onSuccess -> {
			msg.addReaction("dislike:604876349286645780").queue(onSuccess2 -> {
				
			}, onFail2 -> {
				//do nothing
				//System.out.println("Failed to add reaction dislike");
			});;
		}, onFail -> {
			//do nothing
			//System.out.println("Failed to add reaction like");
		});	
	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		
		//Guild g = event.getGuild();
		TextChannel tc = event.getChannel();
		
		if(event.getUser().isBot() || event.getUser().isFake()) {
			return;
		}
		
		if(IDS.contains(tc.getIdLong())) {
			//System.out.println("Reaction added");
			tc.retrieveMessageById(event.getMessageIdLong()).queue(message -> {
				for(MessageReaction r : message.getReactions()) {
					//System.out.println(event.getReactionEmote().getName());
					if(event.getReactionEmote().getName().equals("like")) {
						if(r.getReactionEmote().getName().equals("dislike")) {
							r.removeReaction(event.getUser()).queue();
						}
					}
					
					else if(event.getReactionEmote().getName().equals("dislike")) {
						if(r.getReactionEmote().getName().equals("like")) {
							r.removeReaction(event.getUser()).queue();
						}
					}
				}
			});
			
		}
		
	}

}
