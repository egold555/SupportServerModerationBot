package org.golde.discordbot.supportserver.event;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class WatchYourProfanity extends AbstractMessageChecker {

	List<String> naughtyWords = new ArrayList<String>();
	
	public WatchYourProfanity() {
		loadEnglishWords();
	}
	
	private void loadEnglishWords() {
		try {
			Scanner scanner = new Scanner(new File("res/profanity/words.txt"));
			while(scanner.hasNext()) {
				naughtyWords.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		String text = msg.getContentStripped();
		for(String bad : naughtyWords) {
			if(text.toLowerCase().contains(bad.toLowerCase())){
				return true;
			}
		}
		return false;
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		TextChannel ch = msg.getTextChannel();
		ch.sendMessage("Please " + target.getAsMention() + " ,").queue();
		ch.sendFile(new File("res/profanity/image.png")).queue();
		msg.delete().queue();
	}

}
