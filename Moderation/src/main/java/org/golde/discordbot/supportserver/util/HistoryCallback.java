package org.golde.discordbot.supportserver.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

public class HistoryCallback implements Consumer<MessageHistory> {
	private TextChannel tc;
	private Consumer<List<Message>> messageConsumer;
	private List<Message> messages;
	
	public HistoryCallback(TextChannel tc, Consumer<List<Message>> messageConsumer) {
		this.tc = tc;
		this.messageConsumer = messageConsumer;
		this.messages = new ArrayList<Message>();
	}
	
	@Override
	public void accept(MessageHistory mh) {
		if (mh.isEmpty()) {
			messageConsumer.accept(messages);
		}
		else {
			 List<Message> chunk = mh.getRetrievedHistory();
			 //there are duplicates for some reason. idfk why.
			 for(Message m : chunk) {
				 if(!messages.contains(m)) {
					 messages.add(m);
				 }
			 }
			// messages.addAll(chunk);
        	 Message lastMessage = chunk.get(chunk.size() - 1);
             tc.getHistoryAfter(lastMessage.getId(), 100).queue(this);
		}
	}
}
