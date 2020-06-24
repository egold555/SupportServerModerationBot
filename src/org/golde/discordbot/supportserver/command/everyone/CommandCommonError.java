package org.golde.discordbot.supportserver.command.everyone;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.golde.discordbot.supportserver.constants.Channels;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;

public class CommandCommonError extends EveryoneCommand {

	private HashMap<String[], Long> keysToIds = new HashMap<String[], Long>();

	private String helpErrorBuilt = "";

	public CommandCommonError() {
		super("commonerror", "<error>", "Prints a link to a common error. Leave <error> blank for a list of common errors", "ce", "ec");

		//keysToIds.put(new String[] {""}, L);

		keysToIds.put(new String[] {"addLayer"}, 637484900169023499L);

		keysToIds.put(new String[] {"javax", "vecmath"}, 643882343911915541L);

		keysToIds.put(new String[] {"EntityRenderer", "$1"}, 644343295853723662L);

		keysToIds.put(new String[] {"star"}, 646065217897234433L);

		keysToIds.put(new String[] {"compilejava8"}, 648306714474709002L);

		keysToIds.put(new String[] {"decompileram", "decompram", "mcpram"}, 652053663828672532L);

		keysToIds.put(new String[] {"predicate", "MouseOverFinder"}, 654951373070139402L);
		
		keysToIds.put(new String[] {"1.12keybinds", "112keybinds", "1.12.2keybinds"}, 669275068874096661L);
		
		keysToIds.put(new String[] {"pixel", "pixelFormat", "pf"}, 689268615484407846L);
		
		keysToIds.put(new String[] {"pack",  "rpi", "resourcepack"}, 693350938076905474L);
		
		keysToIds.put(new String[] {"-Xincgc", "xincgc"}, 683555004048867348L);
		
		keysToIds.put(new String[] {"flip()", "ByteBuffer"}, 697648850365841509L);
		
		keysToIds.put(new String[] {"1710scaled"}, 656924417997144114L);
		
		keysToIds.put(new String[] {"mcp-source", "mcp1.6"}, 648306714474709002L);

		for(String[] keys : keysToIds.keySet()) {
			String keyList = Arrays.toString(keys);

			helpErrorBuilt += keyList + " - " + keysToIds.get(keys) + "\n";
		}
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {


		//Member member = event.getMember();

		if(args.size() != 2) {
			replyError(event.getChannel(), "Please specify a error", helpErrorBuilt);
			return;
		}

		String error = args.get(1);

		for(String[] keys : keysToIds.keySet()) {
			for(String key : keys) {
				if(error.equalsIgnoreCase(key)) {
					printError(event, keysToIds.get(keys));
				}
			}
		}

	}

	private void printError(CommandEvent event, long err) {
		TextChannel tc = event.getGuild().getTextChannelById(Channels.COMMON_ERRORS);


		tc.retrieveMessageById(err).queue(onSuccess -> {

			String textMsg = "This question has been answered before here: " + onSuccess.getJumpUrl();
			String title = "Please see " + tc.getName() + ".";
			replySuccess(event.getTextChannel(), title, textMsg);

		});
	}

}
