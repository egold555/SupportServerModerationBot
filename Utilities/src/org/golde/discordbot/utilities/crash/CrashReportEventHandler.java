package org.golde.discordbot.utilities.crash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.utilities.commonerror.CommonError;
import org.golde.discordbot.utilities.commonerror.CommonErrorManager;

import com.opencsv.CSVReader;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CrashReportEventHandler extends EventBase {

	public CrashReportEventHandler(ESSBot bot) {
		super(bot);
	}

	private static HashMap<String, String> errorToMessageJava = new HashMap<String, String>();
	static {

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

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		//		if(!event.getChannel().getName().startsWith("t-")) {
		//			return;
		//		}

				if(!event.getMember().isOwner()) {
					return;
				}

		if(
				event.getChannel().getIdLong() == Channels.INeedHelp.HELP_WITH_ERROR_MESSAGES ||
				event.getChannel().getIdLong() == Channels.INeedHelp.JAVA_HELP ||
				event.getChannel().getIdLong() == Channels.INeedHelp.MCP_HELP ||
				event.getChannel().getIdLong() == Channels.INeedHelp.MY_CODE_ISNT_WORKING ||
				event.getChannel().getIdLong() == Channels.MiscellaneousChats.BOT_COMMANDS
				) {

			checkPastes(event.getMember(), event.getChannel(),event.getMessage().getContentStripped());
			checkFiles(event.getMember(), event.getChannel(), event.getMessage().getAttachments());
		}
		//checkMessage(event.getMember(), event.getChannel(), event.getMessage().getContentStripped());
	}



	private void newCrashReporter(Member sender, TextChannel channel, String text) throws IOException, ParseException {

		//System.out.println(crashFile.getAbsolutePath());

		CrashReport report = CrashReportParser.parse(text);

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

	private void checkForCommonError(Member sender, TextChannel channel, InputStream input) {

		try {

			if(sender.getUser().isBot() || sender.getUser().isFake()) {
				return;
			}

			String msg = new BufferedReader(
					new InputStreamReader(input, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining("\n"));



			if(msg.isEmpty()) {
				return;
			}

			if(!isMinecraftCrashReport(msg)) {
				//sendUpdateMessage(channel, ":x: Not a crash report.");
				return;
			}

			sendUpdateMessage(channel, "Parsing crash data... This might take a moment.");

			boolean foundCommonError = false;

			for(CommonError ce : CommonErrorManager.getCommonErrors()) {
				if(ce.getCrashReport() != null) {
					for(String trigger : ce.getCrashReport()) {
						if(msg.contains(trigger)) {
							foundCommonError = true;
							CommonErrorManager.sendCEMessage(bot, channel, ce);
							break;
						}
					}
				}
			}

			if(!foundCommonError) {
				newCrashReporter(sender, channel, msg);
			}


		}
		catch(Exception e) {
			anErrorOccurred(channel, input, e);
		}

	}



	private void sendUpdateMessage(TextChannel tc, String msg) {
		tc.sendMessage("[Crash Report Identifier] " + msg).queue();
	}

	private boolean isMinecraftCrashReport(String s) {
		return s.split("\n")[0].contains("---- Minecraft Crash Report ----");
	}

	private void checkPastes(Member sender, TextChannel channel, String message) {

		List<String> urlsInMessage = extractUrls(message);
		//System.out.println(urlsInMessage.toString());

		for(String s : urlsInMessage) {
			boolean yes = false;
			if(s.startsWith("https://pastebin.com/")) {
				s = s.replace("https://pastebin.com/", "https://pastebin.com/raw/");
				yes = true;
			}
			else if(s.startsWith("https://hastebin.com/")) {
				s = s.replace("https://hastebin.com/", "https://hastebin.com/raw/");
				if(s.contains(".")) {
					//s = s.split("\\.")[0];
				}
				
				yes = true;
			}

			if(yes) {

				try {
				//	System.out.println(s);
					URLConnection conn = new URL(s).openConnection();
					conn.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
					InputStream input = conn.getInputStream();
					checkForCommonError(sender, channel, input);
				}
				catch(Throwable err) {
					anErrorOccurred(channel, "Link: " + s, err);
				}

			}

		}

	}
	
	private void anErrorOccurred(TextChannel tc, Throwable err) {
		anErrorOccurred(tc, "", err);
	}
	
	private void anErrorOccurred(TextChannel tc, InputStream file, Throwable err) {
		
		String text = null;
		
		if(file != null) {
			text = new BufferedReader(
					new InputStreamReader(file, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining("\n"));
		}
		
		anErrorOccurred(tc, text, err);
	}

	private void anErrorOccurred(TextChannel tc, String file, Throwable err) {
		err.printStackTrace();
		sendUpdateMessage(tc, ":x: An internal error has occurred while parsing your crash report. I have passed this information onto Eric.");
		try {
			String uuid = UUID.randomUUID().toString();
			TextChannel errorChannel = tc.getGuild().getTextChannelById(Channels.OwnerOnly.UNKNOWN_CRASH_REPORTS);
			errorChannel.sendFile(toStringException(err).getBytes(), uuid + " Exception.txt").queue();
			if(file != null && !file.isEmpty()) {
				errorChannel.sendFile(file.getBytes(), uuid + " Crash Report.txt").queue();
			}
			
		}
		catch(IOException ignored) {};
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

		File folder = new File(System.getProperty("java.io.tmpdir"), "ESSUtilityBot");
		if(!folder.exists()) {
			folder.mkdir();
		}

		attachment.downloadToFile(new File(folder, UUID.randomUUID().toString() + ".txt")).thenAccept(in -> {



			try {
				checkForCommonError(sender, channel, new FileInputStream(in));
			} catch (FileNotFoundException e) {
				anErrorOccurred(channel, e);
			}

		})
		.exceptionally(t -> { // handle failure
			this.anErrorOccurred(channel, t);
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

	//https://stackoverflow.com/a/28269120/11245667
	static List<String> extractUrls(String text) {
		List<String> containedUrls = new ArrayList<String>();
		String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = pattern.matcher(text);

		while (urlMatcher.find())
		{
			containedUrls.add(text.substring(urlMatcher.start(0),
					urlMatcher.end(0)));
		}

		return containedUrls;
	}

}
