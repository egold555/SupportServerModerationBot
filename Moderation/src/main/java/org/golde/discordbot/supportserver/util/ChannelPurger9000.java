package org.golde.discordbot.supportserver.util;

import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

//Thanks Supergrupgr for finding a issue with the bot.
//Hopefully this class solves it.
public class ChannelPurger9000 implements Consumer<Void> {

	private TextChannel tc;
	private Runnable callback;
	
	public ChannelPurger9000(TextChannel tc, Runnable callback) {
		this.tc = tc;
		this.callback = callback;
	}
	
	@Override
	public void accept(Void v) {
		
		tc.getHistory().retrievePast(100).queue(messages -> {
			if(messages.isEmpty()) {
				callback.run();
				return;
			}
			try {
				tc.deleteMessages(messages).queue(this);
			}
			catch(IllegalArgumentException e) {
				//if there is only less then 2 msgs
				for(Message m : messages) {
					m.delete().queue();
				}
			}
		});
		
		
	}

}
