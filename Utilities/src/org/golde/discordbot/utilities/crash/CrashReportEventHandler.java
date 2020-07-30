package org.golde.discordbot.utilities.crash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.EventBase;

import com.opencsv.CSVReader;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CrashReportEventHandler extends EventBase {

	public CrashReportEventHandler(ESSBot bot) {
		super(bot);
	}

	private static HashMap<String, Long> errorToIds = new HashMap<String, Long>();
	private static HashMap<String, String> errorToMessage = new HashMap<String, String>();
	private static HashMap<String, String> errorToMessageJava = new HashMap<String, String>();
	static {
		reloadDB();
		
		try {
			CSVReader reader = new CSVReader(new FileReader("res/java-exported-exceptions.csv"));
			
			String [] nextLine;
			// prints the following for the line in your question
			while ((nextLine = reader.readNext()) != null) {
			    
				String match = nextLine[0];
				String url = nextLine[1];
				errorToMessageJava.put(match, "Here is the Javadoc for that exception: " + url);
			}
			
			reader.close();
			
			
		} 
		catch (IOException e) {
			System.err.println("Failed to read res/java-exported-exceptions.csv");
			e.printStackTrace();
		}
		
	}

	public static void reloadDB() {
		errorToMessage.clear();
		errorToIds.clear();

		try {
			CSVReader reader = new CSVReader(new FileReader("res/auto-common-error.csv"));

			String [] nextLine;
			// prints the following for the line in your question
			while ((nextLine = reader.readNext()) != null) {

				String match = nextLine[0];
				String message = nextLine[1];
				if(Character.isDigit(message.charAt(0))) {
					long channelId = Long.parseLong(message.replace("L", ""));
					errorToIds.put(match, channelId);
				}
				else {
					errorToMessage.put(match, message);
				}
			}

			reader.close();


		} 
		catch (IOException e) {
			System.err.println("Failed to read res/auto-common-error.csv");
			e.printStackTrace();
		}

	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

//		if(!event.getChannel().getName().startsWith("t-")) {
//			return;
//		}

		//		if(!event.getMember().isOwner()) {
		//			return;
		//		}

		checkFiles(event.getMember(), event.getChannel(), event.getMessage().getAttachments());
		//checkMessage(event.getMember(), event.getChannel(), event.getMessage().getContentStripped());
	}



	private void newCrashReporter(Member sender, TextChannel channel, File crashFile) throws FileNotFoundException, ParseException {

		//System.out.println(crashFile.getAbsolutePath());

		CrashReport report = CrashReportParser.parse(crashFile);

		if(report == null) {
			
			return;
		}

		StringBuilder builder = new StringBuilder("Looks like you got a **");

		int startStacktrace = 0;
		for(int i = 0; i < report.getException().getBody().length; i++) {
			String line = report.getException().getBody()[i];
			//System.err.println("Searching for: " + line);
			if(!line.contains(" ") && line.length() > 3) {
				startStacktrace = i;
				break;
			}
		}

		String[] split = report.getException().getBody()[startStacktrace].split("\\.");

		int found = 0;
		for(int i = 0; i < split.length; i++) {
			if(split[i].contains("(")) {
				found = i;
				break;
			}
		}

		String path = combine(split, 0, found, ".");
		String linetest = combine(split, found, split.length, ".");
		//String function = linetest.split("\\(")[0];

		Matcher m = Pattern.compile("\\((.*?)\\)").matcher(linetest);
		m.find();
		String classAndLine = m.group(1);
		String[] classesSplit = classAndLine.split(":");
		path = path.substring(0, path.length() - 1); //remove last .
//		System.out.println(Arrays.toString(split));
//		System.out.println(linetest);
//		System.out.println(function);
//		System.out.println(path);
//		System.out.println(classAndLine);
//		System.out.println(Arrays.toString(classesSplit));
//		System.out.println(report.toString());

		builder.append(report.getException().getType() + "** on line **" + classesSplit[1] + "** in the class ** " + path + "**");
		
		if(errorToMessageJava.containsKey(report.getException().getType())) {
			builder.append("\n\n").append(errorToMessageJava.get(report.getException().getType()));
		}
		

		channel.sendMessage("[Crash Report Identifier] :white_check_mark: " + builder.toString()).queue();


		//		}
		//		catch(Exception e) {
		//			e.printStackTrace();
		//			channel.sendMessage("[Crash Report Identifier] :x: An internal error has occurred while parsing your crash report. I have passed this information onto Eric. Attempting to use the old crash report parser...").queue();
		//			//TODO: Send report to me, exception, and to use the old function to parse based on common error.
		//		}



	}

	private void checkForCommonError(Member sender, TextChannel channel, File crashFile) {

		try {
			
			if(sender.getUser().isBot() || sender.getUser().isFake()) {
				return;
			}

			String msg = "";

			StringBuilder contentBuilder = new StringBuilder();
			Stream<String> stream = Files.lines( crashFile.toPath(), StandardCharsets.ISO_8859_1); //UTF doesn't work sometimes, and ISO_8859_1 is a catchall  https://stackoverflow.com/questions/26268132/all-inclusive-charset-to-avoid-java-nio-charset-malformedinputexception-input
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
			msg = contentBuilder.toString();
			stream.close();

			if(msg.isEmpty()) {
				return;
			}

			if(!isMinecraftCrashReport(msg)) {
				//sendUpdateMessage(channel, ":x: Not a crash report.");
				return;
			}

			sendUpdateMessage(channel, "Parsing crash data... This might take a moment.");

			boolean foundCommonError = false;

			for(String key : errorToIds.keySet()) {
				if(msg.contains(key)) {
					foundCommonError = true;
					sendUpdateMessage(channel, errorToIds.get(key));
					break;
				}

			}

			
			for(String key : errorToMessage.keySet()) {
				if(msg.contains(key)) {
					foundCommonError = true;
					sendUpdateMessage(channel, ":white_check_mark: " + errorToMessage.get(key));
					break;
				}

			}
			
//			if(!success) {
//				//if we did not match anything to the common error, we now call the new crash reporter
//				newCrashReporter(sender, channel, crashFile);
//			}
			//.out.println("We got to here 3");
			if(!foundCommonError) {
				newCrashReporter(sender, channel, crashFile);
			}
			

		}
		catch(Exception e) {
			sendUpdateMessage(channel, ":x: An internal error has occurred while parsing your crash report. I have passed this information onto Eric.");
			
			channel.getGuild().getTextChannelById(Channels.UNKNOWN_CRASH_REPORTS).sendFile(crashFile, UUID.randomUUID() + " Crash Report.txt").queue();
			
			try {
				String ex = toStringException(e);
				channel.sendMessage("```" + ex + "```");
				channel.getGuild().getTextChannelById(Channels.UNKNOWN_CRASH_REPORTS).sendMessage("```" + ex + "```").queue();
			}
			catch(IOException ignored) {};
			
			
			e.printStackTrace();
		}

	}
	
	
	
	private void sendUpdateMessage(TextChannel tc, String msg) {
		tc.sendMessage("[Crash Report Identifier] " + msg).queue();
	}
	
	private void sendUpdateMessage(TextChannel tc, long err) {

		TextChannel commonErrors = tc.getGuild().getTextChannelById(Channels.COMMON_ERRORS);

		commonErrors.retrieveMessageById(err).queue(onSuccess -> {
			sendUpdateMessage(tc, ":white_check_mark: Please see " + commonErrors.getAsMention() + ". A fix for this crash can be found here: " + onSuccess.getJumpUrl());
		});
	}

	private boolean isMinecraftCrashReport(String s) {
		return s.split("\n")[0].contains("---- Minecraft Crash Report ----");
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

		File folder = new File(System.getProperty("java.io.tmpdir"), "SupportServerBot");
		folder.mkdir();

		attachment.downloadToFile(new File(folder, UUID.randomUUID().toString() + ".txt")).thenAccept(in -> {


			checkForCommonError(sender, channel, in);
			
		})
		.exceptionally(t -> { // handle failure
			t.printStackTrace();
			sendUpdateMessage(channel, ":x: An internal error has occurred while parsing your crash report. I have passed this information onto Eric. Error code: 2");
			try {
				channel.getGuild().getTextChannelById(Channels.UNKNOWN_CRASH_REPORTS).sendMessage("```" + toStringException(t) + "```").queue();
			}
			catch(IOException ignored) {};
			return null;
		});

	}

	static String combine(String[] arr, int starting, int ending, String combineChar) {
		StringBuilder builder = new StringBuilder();
		for(int i = starting; i < ending; i++) {
			builder.append(arr[i] + combineChar);
		}
		return builder.toString();
	}
	
	static String toStringException(Throwable t) throws IOException {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String toReturn = sw.toString();
		pw.close();
		sw.close();
		return toReturn;
	}

}
