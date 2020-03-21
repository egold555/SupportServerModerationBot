package org.golde.discordbot.supportserver.command.owner;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.golde.discordbot.supportserver.util.SuggestionImageGenerator;
import org.golde.discordbot.supportserver.util.SuggestionImageGenerator.Suggestion;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.entities.TextChannel;

public class CommandTest extends OwnerCommand {

	private final EventWaiter waiter;

	public CommandTest(EventWaiter waiter) {
		super("test", null, "Its for testing, thats all it does");
		this.waiter = waiter;
		this.name = "test";
		this.help = "Its for testing, thats all it does";
		this.requiredRole = "Founder";
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel channel = event.getTextChannel();

		BufferedImage bm = new BufferedImage(10000, 10000, BufferedImage.TYPE_INT_ARGB);
		List<Suggestion> polls = new ArrayList<Suggestion>();
		polls.add(new Suggestion("Example #1", Math.random()));
		polls.add(new Suggestion("Another one with a super long key", Math.random()));
		polls.add(new Suggestion("3rd one!", Math.random()));
		polls.add(new Suggestion("4rd one!", Math.random()));
		polls.add(new Suggestion("5rd one!", Math.random()));
		polls.add(new Suggestion("6rd one!", Math.random()));

		int[] cropXY = SuggestionImageGenerator.paint((Graphics2D) bm.getGraphics(), polls, 0, 0);
		BufferedImage bi = bm.getSubimage(0, 0, cropXY[0], cropXY[1]);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write( bi, "PNG", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			channel.sendFile(imageInByte, "img.png").queue();;
			baos.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		//		channel.getHistoryFromBeginning(1).queue(history ->
		//	     {
		//	         if (!history.isEmpty())
		//	         {
		//	             Message firstMsg = history.getRetrievedHistory().get(0);
		//	             channel.sendMessage(firstMsg).queue();
		//	         }
		//	         else
		//	             channel.sendMessage("No history for this channel!").queue();
		//	     });


	}

}
