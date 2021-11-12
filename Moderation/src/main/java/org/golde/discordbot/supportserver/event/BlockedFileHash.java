package org.golde.discordbot.supportserver.event;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.shared.db.ICanHasDatabaseFile;
import org.golde.discordbot.shared.event.EventBase;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BlockedFileHash extends EventBase implements ICanHasDatabaseFile {

	public BlockedFileHash(ESSBot bot) {
		super(bot);
	}

	private static final String FILE_NAME = "blocked-file-hashes";
	private static List<String> BLOCKED_HASHES;

	private static final Pattern pattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");



	@Override
	public void reload() {
		BLOCKED_HASHES = FileUtil.loadArrayFromFile(FILE_NAME, String[].class);
	}

	//	public static void addUrl(String url) {
	//		BLOCKED_HASHES.add(url);
	//		FileUtil.saveToFile(BLOCKED_HASHES, FILE_NAME);
	//	}
	//
	//	public static List<String> getUrls() {
	//		return BLOCKED_HASHES;
	//	}
	//	
	//	public static boolean remove(String url) {
	//		boolean success = BLOCKED_HASHES.remove(url);
	//		if(success) {
	//			FileUtil.saveToFile(BLOCKED_HASHES, FILE_NAME);
	//		}
	//		return success;
	//	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Message msg = event.getMessage();
		Member target = event.getMember();

		if(target == null || target.getUser() == null || target.getUser().isBot() || event.isWebhookMessage()) {
			return;
		}

		if(msg.getType() != MessageType.DEFAULT) {
			return;
		}

		Matcher matcher = pattern.matcher(msg.getContentStripped());

		//Currently only blocking videos
		if(matcher.find()) {
			String urlStr = matcher.group();
			System.out.println(urlStr);
			try {
				URL url = new URL(urlStr);
				URLConnection c = url.openConnection();
				c.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

				if(urlStr.endsWith(".mp4") || urlStr.endsWith(".mov") || urlStr.endsWith(".webm") || urlStr.endsWith(".mp3") || urlStr.endsWith(".ogg") || urlStr.endsWith(".wav")) {
					doWeNeedToBlockIt(msg, c.getInputStream());
				}
				else {
					String conType = c.getContentType();
					
					if(conType != null) {
						conType = conType.toLowerCase();
						if(conType.equals("video/mp4") || conType.equals("application/mp4") || conType.equals("video/webm") || conType.equals("video/quicktime")) {
							doWeNeedToBlockIt(msg, c.getInputStream());
						}
					}

				}

			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}


		List<Attachment> attachments = msg.getAttachments();
		if(attachments.size() > 0) {

			for(Attachment a : attachments) {

				if(a.isVideo()) {
					a.retrieveInputStream().thenAccept(in -> {
						doWeNeedToBlockIt(msg, in);

					});
				}

			}

		}


	}

	private void doWeNeedToBlockIt(Message msg, InputStream in) {
		try {
			String md5 = getMD5(in);
			System.out.println(md5);

			if(BLOCKED_HASHES.contains(md5) ) {
				//delete their message
				msg.delete().queue();

				//send them a message
				replyError(msg.getChannel(), "That file is blocked on this server. Please contact a Moderator if you think this is a mistake.", 10);
			}


		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String getMD5(InputStream is) throws NoSuchAlgorithmException, IOException {
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = is.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		is.close();
		byte[] digest = complete.digest();

		String result = "";

		for (int i=0; i < digest.length; i++) {
			result += Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result.toUpperCase();
	}

}
