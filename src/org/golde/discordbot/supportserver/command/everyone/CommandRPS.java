package org.golde.discordbot.supportserver.command.everyone;

import java.util.List;

import org.golde.discordbot.supportserver.command.BaseCommand;
import org.golde.discordbot.supportserver.constants.CustomEmotes;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;

public class CommandRPS extends BaseCommand {

	private static final String EMOJI_ROCK = CustomEmotes.ROCK;
	private static final String EMOJI_PAPER = "🗞️";
	private static final String EMOJI_SCISSORS = "✂️";
	private static final String EMOJI_TIE = "👔";

	public CommandRPS() {
		this.name = "rps";
		this.help = "Play a game of Rock Paper Scissors";
		this.arguments = "<rock | paper | scissors>";
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		if(args.size() > 0)
		{
			String hand = "", emoji = "";
			String hand2 = getHand();
			if("rock".equals(args.get(1).toLowerCase()) || "rocks".equals(args.get(1).toLowerCase()) || "r".equals(args.get(1).toLowerCase()) || "stone".equals(args.get(1).toLowerCase()))
			{
				emoji = EMOJI_ROCK;
				hand = "rock";
			}
			else if("paper".equals(args.get(1).toLowerCase()) || "papers".equals(args.get(1).toLowerCase()) || "p".equals(args.get(1).toLowerCase()))
			{
				emoji = EMOJI_PAPER;
				hand = "paper";
			}
			else if("scissor".equals(args.get(1).toLowerCase()) || "scissors".equals(args.get(1).toLowerCase()) || "s".equals(args.get(1).toLowerCase()))
			{
				emoji = EMOJI_SCISSORS;
				hand = "scissors";
			}
			else
			{
				event.replyError("Please enter a valid choice. (**r**ock | **p**aper | **s**cissors)");
				return;
			}

			String output = compare(hand, hand2);
			EmbedBuilder b = new EmbedBuilder();
			b.setTitle("Rock Paper Scissors");
			b.addField("Player 1:", event.getJDA().getSelfUser().getAsTag(), false);
			b.addField("Player 2:", event.getAuthor().getAsTag(), false);
			b.addField("-----------------------------", "", false);
			b.addField("Result:", output, false);
			b.addField("Player #1:", emoji2, false);
			b.addField("Player #2:", emoji, false);
			
			event.getChannel().sendMessage(b.build()).queue();;
			//event.getChannel().sendMessage("\n\n**" + output + "**\n\nYou: " + emoji + " Me: " + emoji2 + "\n\n").queue();
		}
		else {
			event.replyError("Please enter a valid choice. ;rps (**r**ock | **p**aper | **s**cissors)");
		}
	}

	private String emoji2 = "";
	private String getHand()
	{
		String hand = "";
		int choice = randomNum(1, 3);
		switch(choice)
		{
		case 1: hand = "rock";
		emoji2 = EMOJI_ROCK;
		break;
		case 2: hand = "paper";
		emoji2 = EMOJI_PAPER;
		break;
		case 3: hand = "scissors";
		emoji2 = EMOJI_SCISSORS;
		break;
		default: hand = "no hand";
		break;
		}
		return hand;
	}

	private static String compare(String hand, String hand2)
	{
		String result = "";
		if(hand.equals(hand2))
			result = EMOJI_TIE + " It's a tie!";
		else if(hand.equals("rock"))
		{
			if(hand2.equals("paper"))
				result = "Player #1 won!";
			if(hand2.equals("scissors"))
				result = "Player #2 won!";
		}
		else if(hand.equals("paper"))
		{
			if(hand2.equals("scissors"))
				result = "Player #1 won!";
			if(hand2.equals("rock"))
				result = "Player #2 won!";
		}
		else if(hand.equals("scissors"))
		{
			if(hand2.equals("rock"))
				result = "Player #1 won!";
			if(hand2.equals("paper"))
				result = "Player #2 won!";
		}

		return result;
	}

	private static int randomNum(int start, int end) {

		if(end < start) {
			int temp = end;
			end = start;
			start = temp;
		}

		return (int) Math.floor(Math.random() * (end - start + 1) + start);
	}

}
