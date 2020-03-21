package org.golde.discordbot.supportserver.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.golde.discordbot.supportserver.constants.Channels;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoCommonError extends ListenerAdapter {


	private HashMap<String[], Long> errorToIds = new HashMap<String[], Long>();
	private HashMap<String[], String> errorToMessage = new HashMap<String[], String>();

	//isolate the exception from the rest of the file
	//Pattern p;


	public AutoCommonError() {
		errorToIds.put(new String[] {
				"java.lang.NoSuchMethodError: net.minecraft.client.renderer.EntityRenderer$1.<init>(Lnet/minecraft/client/renderer/EntityRenderer;)V",
		"java.lang.NoSuchMethodError: 'void net.minecraft.client.renderer.EntityRenderer$1.<init>(net.minecraft.client.renderer.EntityRenderer)'"},
				644343295853723662L);


		errorToIds.put(new String[] {
				"The method addLayer(U) in the type RendererLivingEntity<AbstractClientPlayer> is not applicable for the arguments (LayerCape)"
		}, 
				637484900169023499L);

		errorToMessage.put(new String[] {
				"Caused by: java.net.ConnectException: Connection refused: connect"
		}, 
				"Check to make sure the website your pinging is online!");

		errorToIds.put(new String[] {
				"$MouseOverFinder cannot be cast to class java.util.function.Predicate"
		}, 
				654951373070139402L);

		errorToMessage.put(new String[] {
				"com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT"
		}, 
				"Looks like a method tried to parse non JSON data as JSON data. I would check to make sure the website you are requesting data from is actually returning JSON data.");

//		errorToMessage.put(new String[] {
//				"java.lang.NullPointerException: Initializing game"
//		}, 
//				"Looks like something was null. I would put print statements before your object method calls to check if that object was null.");

		errorToMessage.put(new String[] {"java.lang.NoSuchMethodError: java.nio.ByteBuffer.flip()Ljava/nio/ByteBuffer;"}, "This is a strange issue, but it seems to be fixed by: `casting ByteBuffer instances to Buffer before calling the method.` I would also double check in your IIDE your compiling with Java 8. Just because you have Java 8 Installed, does not mean your IDE is compiling with it. For more detail: please see https://github.com/apache/felix/pull/114 and https://www.google.com/search?q=java.lang.NoSuchMethodError:%20java.nio.ByteBuffer.flip()Ljava/nio/ByteBuffer;");

		errorToMessage.put(new String[] {"java.lang.NoClassDefFoundError: net/arikia/dev/drpc/DiscordEventHandlers"}, "Looks like you did not shade in the Discord library from your libs folder. Make sure to shade in **every** library in your libs folder to your jar before running it outside of eclipse!");
		
		errorToMessage.put(new String[] {"java.lang.NullPointerException: Registering texture"}, "Looks like you are trying to register a null texture. I would add some print statements or breakpoints to figure out why your texture is null.");
		
		errorToMessage.put(new String[] {"java.lang.IndexOutOfBoundsException"}, "Looks like you are trying to access a value in a list at a index that is not valid. You can read about it more here https://docs.oracle.com/javase/7/docs/api/java/lang/IndexOutOfBoundsException.html");
		
		errorToMessage.put(new String[] {"org.lwjgl.LWJGLException: Pixel format not accelerated"}, "There are a few issues that could cause this. Outdated graphics card, or remote viewing through Remote Desktop connection. Here is a helpful article by the mojang team about your issue: https://minecrafthopper.net/help/pixel-format-not-accelerated/");
		
		errorToIds.put(new String[] {"net.minecraft.client.settings.KeyBinding.compareTo(KeyBinding.java:"}, 669275068874096661L);
		
		//keysToIds.put(new String[] {"javax", "vecmath"}, 643882343911915541L);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

//		if(event.getChannel().getIdLong() != 604769552416374807L) {
//			return;
//		}

		checkFiles(event.getMember(), event.getChannel(), event.getMessage().getAttachments());
		//checkMessage(event.getMember(), event.getChannel(), event.getMessage().getContentStripped());
	}



	private void checkMessage(Member sender, TextChannel channel, String msg) {

		if(sender.getUser().isBot()) {
			return;
		}

		if(msg.isEmpty()) {
			return;
		}

		if(!isMinecraftCrashReport(msg)) {
			return;
		}

		channel.sendMessage("[Crash Report Identifier] Parsing crash data... This might take a moment.").queue();

//		p = Pattern.compile("(?m)^.*?Exception.*(?:[\\r\\n]+^\\s*at .*)");
//		Matcher m = p.matcher(msg);
//
//		
//		if(m.find()) {
//			for(int i = 0; i <= m.groupCount(); i++) {
//				String theGroup = m.group(i);
//				channel.sendMessage("Exception: ```" + theGroup + "```").queue();
//				System.out.println("-----" + theGroup);
//			}
//		}

		boolean success = false;
		
		for(String[] keys : errorToIds.keySet()) {
			for(String key : keys) {
				if(msg.contains(key)) {
					printError(channel, errorToIds.get(keys));
					success = true;
					return;
				}
			}

		}

		for(String[] keys : errorToMessage.keySet()) {
			for(String key : keys) {
				if(msg.contains(key)) {
					channel.sendMessage("[Crash Report Identifier] :white_check_mark: " + errorToMessage.get(keys)).queue();
					success = true;
					return;
				}
			}

		}

		if(!success) {
			channel.sendMessage("[Crash Report Identifier] :x: Could not match data to anything in my memory. I have let Eric know to add this exception into my crash report database. Sorry for the inconveenence.").queue();
			channel.getGuild().getTextChannelById(684115853721075715L).sendFile(msg.getBytes(), sender.getId() + " Crash Report.txt").queue();
		}

	}

	private void printError(TextChannel tc, long err) {


		TextChannel commonErrors = tc.getGuild().getTextChannelById(Channels.COMMON_ERRORS);

		commonErrors.retrieveMessageById(err).queue(onSuccess -> {

			String textMsg = "[Crash Report Identifier] :white_check_mark: Please see " + commonErrors.getAsMention() + ". A fix for this crash can be found here: " + onSuccess.getJumpUrl();

			tc.sendMessage(textMsg).queue();;

		});
	}



	private void checkFiles(Member sender, TextChannel channel, List<Attachment> attachments) {

		if(attachments.size() == 0) {
			return;
		}

		//only check the first one
		Attachment attachment = attachments.get(0);
		//System.out.println("Step 1");

		//we dont want image files
		if(attachment.isImage()) {
			return;
		}

		//System.out.println("Step 2");

		//only looking for text files
		if(!attachment.getFileName().endsWith(".txt") && !attachment.getFileName().endsWith(".log")) {
			return;
		}

		//System.out.println("Step 3");

		attachment.retrieveInputStream().thenAccept(in -> {
			StringBuilder builder = new StringBuilder();
			byte[] buf = new byte[1024];
			int count = 0;
			try {
				while ((count = in.read(buf)) > 0) {
					builder.append(new String(buf, 0, count));
				}

				in.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

			checkMessage(sender, channel, builder.toString());
		})
		.exceptionally(t -> { // handle failure
			t.printStackTrace();
			return null;
		});

	}

	private boolean isMinecraftCrashReport(String s) {
		return s.contains("---- Minecraft Crash Report ----");
	}

}
