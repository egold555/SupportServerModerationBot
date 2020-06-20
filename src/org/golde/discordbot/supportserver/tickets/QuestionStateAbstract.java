package org.golde.discordbot.supportserver.tickets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.golde.discordbot.supportserver.constants.SSEmojis;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class QuestionStateAbstract {

	protected abstract String[] getQuestions();
	
	
	private boolean isFinished = false;
	private int questionIndex = 0;
	private final TextChannel channel;
	private final Member member;

//	private static final String hasNext = " When you are done, please type 'next'.";
//	private static final String hasNone = " If you have none, please type in 'none'";
//	private static final String notApplicable = " If its not applicable, please type in 'none'";
	
	private static final int TIMEOUT = 60 * 5; //5min per question
	
	private List<Message> responses = new ArrayList<Message>();
	
	public static Set<Long> inTemp = new HashSet<Long>();

	public QuestionStateAbstract(TextChannel channel, Member member) {
		this.channel = channel;
		this.member = member;
		inTemp.add(member.getIdLong());
		pingUser();
		
		sendNextQuestion();
	}


	private void pingUser() {
		
		channel.createPermissionOverride(member).queue(onSuccess1 -> {
			onSuccess1.getManager().setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_ATTACH_FILES).queue();
			channel.sendMessage("Hey " + member.getAsMention() + "! Over here!").queue(success -> {
				success.delete().queueAfter(2, TimeUnit.SECONDS);
			});
		});
		
		
	}


	private void finish() {
		isFinished = true;
		inTemp.remove(member.getIdLong());
		TicketManager.create(member, this);
	}

	public void recieved(Message msg) {

		if(msg.getType() == MessageType.DEFAULT) {
			tryInsert(questionIndex, msg);
		}

	}

	private void tryInsert(int index, Message msg) {
		responses.add(msg);
	}

	
	private Message prevMessage = null;
	private static final String NEXT_MESSAGE = "\n\nWhen you are finished. Please click the :fast_forward: reaction";
	
	public void sendNextQuestion() {

		if(prevMessage != null) {
			String text = prevMessage.getContentDisplay();
			text = text.replace(NEXT_MESSAGE, "");
			prevMessage.editMessage(text).queue();
		}
		
		if(questionIndex > getQuestions().length - 1) {
			finish();
			return;
		}
		
		
		
		channel.sendMessage(getQuestions()[questionIndex] + NEXT_MESSAGE).queue(onSuccess -> {
			onSuccess.addReaction(SSEmojis.NEXT).queue();
			prevMessage = onSuccess;
		});
		questionIndex++;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public TextChannel getChannel() {
		return channel;
	}

	public long getMemberId() {
		return member.getIdLong();
	}
	
	public List<Message> getResponses() {
		return responses;
	}

}
