package org.golde.discordbot.supportserver.event;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.command.BaseCommand;
import org.golde.discordbot.supportserver.command.BaseCommand.EnumReplyType;
import org.golde.discordbot.supportserver.constants.Categories;
import org.golde.discordbot.supportserver.constants.Channels;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Countingv2 extends EventBase {

	int currentCount = 1;
	int highScore = 0;
	
	private static final String CHANNEL_NAME = "counting-v2";
	private static final long CATEGORY = Categories.BETA_TESTER;
	
	long prevChatter = -1;

	@Override
	public void onReady(ReadyEvent event) {

		Guild g = event.getJDA().getGuilds().get(0); //only one guild
		TextChannel tc = g.getTextChannelsByName(CHANNEL_NAME, true).get(0);
		
		tc.getHistory().retrievePast(1).queue(success -> {
			currentCount = Integer.parseInt(success.get(0).getContentStripped());
			prevChatter = success.get(0).getAuthor().getIdLong();
		});

		String highscoreString = tc.getTopic();
		highscoreString = highscoreString.replace("Highscore: ", "").replace("*", "");
		highScore = Integer.parseInt(highscoreString);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		checkMessage(event.getChannel(), event.getMember(), event.getMessage());

	}

	@Override
	public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
		checkMessage(event.getChannel(), event.getMember(), event.getMessage());

	}

	void checkMessage(TextChannel tc, Member sender, Message message) {

		if(sender.getUser().isBot() || sender.getUser().isFake()) {
			return;
		}
		
		if(!tc.getName().equals(CHANNEL_NAME)) {
			return;
		}

		tc.getHistory().retrievePast(3).queue(success -> {
			int pastMessage;
			try {
				pastMessage = Integer.parseInt(success.get(1).getContentStripped());
			}
			catch(NumberFormatException e) {
				pastMessage = Integer.parseInt(success.get(2).getContentStripped());
			}
			
			try {
				int currentMessage = Integer.parseInt(message.getContentStripped());
				if(currentMessage > pastMessage && currentMessage < pastMessage + 2) {
					
					if(prevChatter != sender.getIdLong()) {
						//System.out.println(prevChatter + " != " + sender.getIdLong());
						success(tc, currentMessage, sender);
						prevChatter = sender.getIdLong();
					}
					else {
						message.delete().queue();
						replyError(tc, sender.getAsMention() + ", you can't send a number again until a different person sends a number!", 10);
					}
					
				}
				else {
					fail(tc);
					//System.out.println("[F] " + currentMessage + " > " + pastMessage);
				}
			}
			catch(Exception e) {
				//System.out.println("[F] " + e.getMessage());
				fail(tc);
			}

		});

	}

	private void success(TextChannel tc, int currentMessage, Member sender) {
		updateScore(tc, currentMessage);
		
		//make it so the person who last said something does not have permissions anymore to send until somebody differently talks
		
	}

	private void fail(TextChannel tc) {

		//set the hughScore and reset the count
		if(currentCount > highScore) {
			highScore = currentCount;
		}
		
		currentCount = 0;
		prevChatter = -1;

		//resets the channel
		Guild g = tc.getGuild();
		
		tc.getManager().setParent(g.getCategoryById(Categories.TEMP)).queue(success1 -> {
			purgeChannelMessage(tc, () -> {
				tc.getManager().setParent(g.getCategoryById(CATEGORY)).queue();
				tc.sendMessage("1").queue();
				updateScore(tc, 1);
				tc.getManager().setTopic("Highscore: **" + highScore + "**").queue();
			});
		});
		

//		tc.delete().queue(success -> {
//
//			g.createTextChannel(CHANNEL_NAME).setParent(g.getCategoryById(Categories.BETA_TESTER)).queue(success2 -> {
//				success2.sendMessage("1").queue();
//				updateScore(success2, 1);
//				success2.getManager().setTopic("Highscore: **" + highScore + "**").queue();
//			});
//
//		});



	}
	
	
	private boolean isWorking = false;
	//this is so bad but whatever,=. Discord needs better endpoints -.-
	private void purgeChannelMessage(TextChannel tc, Runnable run) {
		
		System.out.println("Deleting messages in channel: " + tc.getName());
		isWorking = true;
		
		new Thread(() -> 
        {
            while (isWorking)
            {
                tc.getHistory().retrievePast(100).queue(messages -> {
                	 if (messages.isEmpty())
                     {
                         isWorking = false;
                        // System.out.println("Done deleting: " + tc.getName());
                         run.run();//idk if this is right but it works
                         return;
                     }
                	 
                	 if(messages.size() < 3) {
                		 isWorking = false;
                		 return;
                	 }
             
                     //messages.forEach(m -> System.out.println("Deleting: " + m));
                     tc.deleteMessages(messages).queue();
                     
                });
                try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               
            }
        })
        .run();
		
	}

	private void updateScore(TextChannel channel, int newCurrentCount) {
		currentCount = newCurrentCount;
		if(newCurrentCount > highScore) {
			highScore = newCurrentCount;
			channel.getManager().setTopic("Highscore: **" + highScore + "**").queue();
		}
	}

}
