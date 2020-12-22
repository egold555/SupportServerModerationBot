package org.golde.discordbot.utilities.event;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.shared.util.SimpleAsyncWebRequest;
import org.golde.discordbot.shared.util.SimpleAsyncWebRequest.WebRequestCallback;
import org.golde.discordbot.shared.util.StringUtil;
import org.golde.discordbot.utilities.commonerror.CommonError;
import org.golde.discordbot.utilities.commonerror.CommonErrorManager;
import org.golde.discordbot.utilities.db.DB;
import org.golde.discordbot.utilities.util.UniqueList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class OCRCommonErrors extends EventBase {

	public OCRCommonErrors(ESSBot bot) {
		super(bot);
	}

	private static final List<String> ALLOWED_FILE_EXTENTIONS = Arrays.asList("png", "jpg", "jpeg");
	private static final List<Long> WHITELISTED_CHANNELS = Arrays.asList(
			Channels.BotDebugging.COMMAND_TESTING, 
			Channels.INeedHelp.MY_CODE_ISNT_WORKING, 
			Channels.INeedHelp.MCP_HELP, 
			Channels.INeedHelp.JAVA_HELP, 
			Channels.INeedHelp.HELP_WITH_ERROR_MESSAGES
			);

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		TextChannel tc = event.getChannel();
		Message m = event.getMessage();

		if(event.getAuthor().isBot() || event.getAuthor().isFake()) {
			return;
		}

		if(WHITELISTED_CHANNELS.contains(tc.getIdLong())) {

			if(m.getAttachments().size() > 0) {

				Attachment a = m.getAttachments().get(0);
				if(a.isImage()) {

					if(a.getFileExtension() != null && ALLOWED_FILE_EXTENTIONS.contains(a.getFileExtension())) {

						if(a.getSize() > 1024000) {
							System.out.println("[OCR] File too big. We need to either resize this, or do something with it.");
							
							return;
						}
						
						String url = "https://api.ocr.space/parse/imageurl?apikey=" + DB.getInstance().getApiKeys().ocr + "&url=" + a.getProxyUrl() + "&scale=true";

						new SimpleAsyncWebRequest(url, new WebRequestCallback() {

							@Override
							public void onResponse(int statusCode, String response) {
								System.out.println(response);

								if(statusCode == 200) {
									doOcr(bot, tc, event.getMember(), response, a);

								}

							}

							@Override
							public void onIOError(IOException ex) {
								sendErrorMessageToChannel(event.getGuild(), "onIoError", StringUtil.toString(ex), a);
								System.err.println("[OCR] Huh, something didnt go right");
								ex.printStackTrace();
							}
						}).send();

					}
					else {
						System.out.println("[OCR] File Not allowed");
					}

				}

			}

		}


	}

	private static void sendErrorMessageToChannel(Guild g, String where, String error, Attachment a) {
		TextChannel tc = g.getTextChannelById(Channels.BotDebugging.OCR_ERRORS);
		tc.sendMessage("**Error Happened**: " + where).queue(success -> {
			if(error == null || error.length() == 0) {
				return;
			}
			if(error.length() >= 1994) {
				tc.sendFile(error.getBytes(), "Exception.txt").queue();
			}
			else {
				tc.sendMessage("```" + error + "```").queue();
			}
		});
		
		tc.sendMessage(a.getUrl()).queue();

//		a.retrieveInputStream().thenAccept(in -> {
//
//			tc.sendFile(in, a.getFileName()).queue();
//			try {
//				in.close();
//			} 
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}).exceptionally(t -> { // handle failure
//			t.printStackTrace();
//			return null;
//		});

	}

	public static void doOcr(ESSBot bot, TextChannel tc, Member mem, String response, Attachment a) {
		Guild g = tc.getGuild();
		JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

		int OCRExitCode = jsonObject.get("OCRExitCode").getAsInt();
		boolean IsErroredOnProcessing = jsonObject.get("IsErroredOnProcessing").getAsBoolean();

		if(IsErroredOnProcessing) {
			sendErrorMessageToChannel(g, "IsErroredOnProcessing", "IsErroredOnProcessing == true", a);
			System.err.println("[OCR] IsErroredOnProcessing == true");
			return;
		}

		if(OCRExitCode != 1) {
			sendErrorMessageToChannel(g, "OCRExitCode Failed", "Code was " + OCRExitCode + " and not 1.", a);
			System.err.println("[OCR] OCRExitCode Failed: " + OCRExitCode);
			return;
		}

		JsonObject parsedResults = jsonObject.get("ParsedResults").getAsJsonArray().get(0).getAsJsonObject();
		int fileParseExitCode = parsedResults.get("FileParseExitCode").getAsBigInteger().intValueExact();
		String errorMessage = parsedResults.get("ErrorMessage").getAsString();
		String errorDetails = parsedResults.get("ErrorDetails").getAsString();

		//failed for some reason
		if(fileParseExitCode != 1) {
			System.err.println("[OCR] Failed: " + fileParseExitCode);
			System.err.println("[OCR]    Error message: " + errorMessage);
			System.err.println("[OCR]    Error details: " + errorDetails);
			sendErrorMessageToChannel(g, "fileParseExitCode", "Error Message: " + errorMessage + "\n\nError Details: " + errorDetails, a);
			return;
		}

		String ParsedText = parsedResults.get("ParsedText").getAsString();

		String[] lines = ParsedText.split("\\r?\\n");

		System.out.println("\n\n===============\n\n" + response + "\n\n===============\n\n");
		System.out.println("Success: ");
		System.out.println(Arrays.toString(lines));

		for(CommonError ce : CommonErrorManager.getCommonErrors()) {

			if(ce.getOcr() != null && ce.getOcr().length > 0) {

				if(contains(lines, ce.getOcr())) {
					CommonErrorManager.sendCEMessage(bot, tc, ce);
				}
				else {
					System.out.println("No match");
				}
			}
			else {
				//System.out.println("OCR line as null");
			}

		}

		//sendErrorMessageToChannel(g, "Failed to find any common errors relating to an image", "", a);

	}

	private static boolean contains(String[] text, String[] text2) {
		
		UniqueList<String> one = new UniqueList<String>();
		UniqueList<String> two = new UniqueList<String>();
		
		for(String s : text) {
			one.add(s.toLowerCase());
		}
		
		for(String s : text2) {
			two.add(s.toLowerCase());
		}
		
		for(String s : text) {
			for(String s2 : text2) {
				if(s.startsWith(s2) || s.endsWith(s2) || s.contains(s2)) {
					return true;
				}
			}
		}
		
		return false;

	}

}
