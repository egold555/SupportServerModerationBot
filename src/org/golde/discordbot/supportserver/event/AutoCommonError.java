package org.golde.discordbot.supportserver.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.golde.discordbot.supportserver.constants.Channels;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoCommonError extends ListenerAdapter {

	
	private HashMap<String[], Long> errorToIds = new HashMap<String[], Long>();
	private HashMap<String[], String> errorToMessage = new HashMap<String[], String>();
	
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
		
		errorToMessage.put(new String[] {
				"java.lang.NullPointerException: Initializing game"
		}, 
		"Looks like something was null. I would put print statements before your object method calls to check if that object was null.");
		
		errorToMessage.put(new String[] {"java.lang.NoSuchMethodError: java.nio.ByteBuffer.flip()Ljava/nio/ByteBuffer;"}, "This is a strange issue, but it seems to be fixed by: `casting ByteBuffer instances to Buffer before calling the method.` For more detail: please see https://github.com/apache/felix/pull/114 and https://www.google.com/search?q=java.lang.NoSuchMethodError:%20java.nio.ByteBuffer.flip()Ljava/nio/ByteBuffer;");
		
		//keysToIds.put(new String[] {"javax", "vecmath"}, 643882343911915541L);
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		if(event.getChannel().getIdLong() != 604769552416374807L) {
			return;
		}
		
		checkFiles(event.getMember(), event.getChannel(), event.getMessage().getAttachments());
		checkMessage(event.getMember(), event.getChannel(), event.getMessage().getContentStripped());
	}
	
	

	private void checkMessage(Member sender, TextChannel channel, String msg) {
		
		if(sender.getUser().isBot()) {
			return;
		}
		
		if(msg.isEmpty()) {
			return;
		}
		
		if(!isMinecraftCrashReport(msg)) {
			channel.sendMessage(":x: Not a crash report").queue();
			return;
		}
		
		channel.sendMessage(":white_check_mark: Is a crash report. Parsing data...").queue();
		
		for(String[] keys : errorToIds.keySet()) {
			for(String key : keys) {
				if(msg.contains(key)) {
					printError(channel, errorToIds.get(keys));
					channel.sendMessage("**Success**").queue();
					return;
				}
			}
			
		}
		
		for(String[] keys : errorToMessage.keySet()) {
			for(String key : keys) {
				if(msg.contains(key)) {
					channel.sendMessage(errorToMessage.get(keys)).queue();
					channel.sendMessage("**Success**").queue();
					return;
				}
			}
			
		}
		
		channel.sendMessage(":white_check_mark: Is a crash report. Parsing data...").queue();
		
	}
	
	private void printError(TextChannel tc, long err) {


		TextChannel commonErrors = tc.getGuild().getTextChannelById(Channels.COMMON_ERRORS);
		
		commonErrors.retrieveMessageById(err).queue(onSuccess -> {

			String textMsg = "Please see " + commonErrors.getAsMention() + ". This question has been answered before here: " + onSuccess.getJumpUrl();

			tc.sendMessage(textMsg).queue();;

		});
	}



	private void checkFiles(Member sender, TextChannel channel, List<Attachment> attachments) {
		
		if(attachments.size() == 0) {
			return;
		}

		//only check the first one
		Attachment attachment = attachments.get(0);
		System.out.println("Step 1");

		//we dont want image files
		if(attachment.isImage()) {
			return;
		}
		
		System.out.println("Step 2");

		//only looking for text files
		if(!attachment.getFileName().endsWith(".txt") && !attachment.getFileName().endsWith(".log")) {
			return;
		}

		System.out.println("Step 3");
		
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
