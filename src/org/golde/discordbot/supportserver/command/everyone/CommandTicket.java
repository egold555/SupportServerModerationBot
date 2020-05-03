package org.golde.discordbot.supportserver.command.everyone;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.command.BaseCommand;
import org.golde.discordbot.supportserver.command.mod.ModCommand;
import org.golde.discordbot.supportserver.constants.Channels;
import org.golde.discordbot.supportserver.constants.SSEmojis;
import org.golde.discordbot.supportserver.util.HistoryCallback;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandTicket extends EveryoneCommand {

	private static final long MCIW_TICKETS_CATEGORY = 699752267481481236L;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss") ;

	public CommandTicket() {
		super("ticket", "[create, close]", "Ticket management command", "t");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		if(args.size() != 2) {
			event.replyError(this.getHelpReply());
		}
		else {
			if(args.get(1).equalsIgnoreCase("create")) {
				tryNewTicket(event.getMember(), event.getChannel());
			}
			//			else if(args.get(1).equalsIgnoreCase("find")) {
			//				TextChannel tc = doesChannelExist(event.getGuild(), getChannelName(event.getMember()));
			//				if(tc != null) {
			//					event.reply("I will ping you in your channel. Just a moment please :)");
			//					ticketAlreadyExists(event.getMember(), tc);
			//				}
			//				else {
			//					event.replyError("Channel does not exist. Please run \"" + BaseCommand.PREFIX + "ticket create\"");
			//				}
			//			}
			else if(args.get(1).equalsIgnoreCase("close")) {
				Member member = event.getMember();

				TextChannel channel = event.getTextChannel();

				String channelNameCompare = channel.getName().substring(2);
				if(ModCommand.isModerator(member) || channelNameCompare.equalsIgnoreCase(member.getEffectiveName().replace(" ", "-"))) {
					closeTicket(event.getGuild(), channel, member);
				}
				else {
					event.replyError("You do not have permission to close this channel.");
				}
			}
		}
	}

	private static void ticketAlreadyExists(Member member, TextChannel theirChannel) {
		sendSelfDestructingMessage(theirChannel, 2, member.getAsMention() + " Your ticket is over here!");
	}

	public static void tryNewTicket(Member member, MessageChannel messageChannel) {

		if(true) {
			sendSelfDestructingMessage(messageChannel, 10, "Hi! Thank you for tring to create a ticket, but unfortunetly, I needed to disable tickets for the time being, so I can add new features, and get some more bugs squashed. For the time being, please use <#594348119689527298>.");
			return;
		}
		
		Guild g = member.getGuild();
		final String channelName = getChannelName(member);

		getOrCreateChannel(g, channelName, new Consumer<TextChannel>() {

			@Override
			public void accept(TextChannel t) {
				//channel already exists
				sendSelfDestructingMessage(t, 10, "Your ticket already exists. Click " + t.getAsMention() + " to quickly jump there.");
				ticketAlreadyExists(member, t);
			}

		}, 
				new Consumer<TextChannel>() {

			@Override
			public void accept(TextChannel t) {

				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Main.EMBED_COLOR);
				eb.setTitle("Welcome to your new ticket!");
				eb.setDescription("Hello " + member.getEffectiveName() + "! This is your new ticket. When you are finished, please click the X reaction to close, or type ```" + BaseCommand.PREFIX + "ticket close``` Only you and Moderators can close tickets. If you ever need to get back to this message, I have pinned it. \n\n If you need to figure out what ticket is yours, do ```" + BaseCommand.PREFIX + "ticket find ``` and I will ping you!");
				eb.setTimestamp(Instant.now());
				eb.setFooter(Main.getJda().getSelfUser().getAsTag(), Main.getJda().getSelfUser().getAvatarUrl());

				sendSelfDestructingMessage(messageChannel, 5, "Your ticket has been created! Click " + t.getAsMention() + " to quickly jump there.");
				t.sendMessage(eb.build()).queue(success -> {
					success.addReaction("❌").queue();
					t.pinMessageById(success.getId()).queue();
				});

				sendSelfDestructingMessage(t, 2, "Hey " + member.getAsMention() + "! Over here!");

				t.getManager().setTopic(member.getId()).queue();


			}
		});



	}

	private static void getOrCreateChannel(Guild g, String name, Consumer<TextChannel> foundCallback, Consumer<TextChannel> createdCallback) {

		TextChannel recieved = doesChannelExist(g, name);
		if(recieved != null) {
			foundCallback.accept(recieved);
			return;
		}

		net.dv8tion.jda.api.entities.Category category = g.getCategoryById(MCIW_TICKETS_CATEGORY);
		category.createTextChannel(name).queue(createdCallback);
	}

	private static TextChannel doesChannelExist(Guild g, String name) {
		List<TextChannel> recieved = g.getTextChannelsByName(name, true);
		if(recieved.size() == 0) {
			return null;
		}
		return recieved.get(0);
	}

	public static void closeTicket(Guild g, String name, Member closer) {
		List<TextChannel> recieved = g.getTextChannelsByName(name, true);
		if(recieved.size() != 0) {
			closeTicket(g, recieved.get(0), closer);
		}
	}

	public static void closeTicket(Guild g, TextChannel channel, Member closer) {

		channel.getManager().putPermissionOverride(g.getPublicRole(), null, EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ)).queue();

		Member member = g.getMemberById(channel.getTopic());

		getAllMessages(channel, messages -> {
			Collections.reverse(messages);

			//save to file
			try {
				String name = dateFormat.format(new Date()) + " - " + member.getEffectiveName() + ".txt";
				String dmMessage = "Hello " + member.getEffectiveName() + "! \nHere is the summary of you ticket.";
				StringBuilder b = new StringBuilder();

				for(Message m : messages) {
					b.append(toStringMessage(m) + "\n");
				}


				//send the file to the person in dms
				member.getUser().openPrivateChannel().queue((dmChannel) -> {
					dmChannel.sendMessage(dmMessage).addFile(b.toString().getBytes(), name).queue();
				});

				MessageEmbed embed = ModLog.getActionTakenEmbed(ModAction.TICKET_CLOSE, g.getSelfMember().getUser(), 
						new String[] {
								"Ticket Holder",
								member.getEffectiveName()
				},
						new String[] {
								"Closer",
								closer.getEffectiveName()
				}
						);
				g.getTextChannelById(Channels.MOD_LOGS).sendMessage(embed).addFile(b.toString().getBytes(), name).queue(success -> {
					channel.delete().queue();
				});

				//				g.getTextChannelById(Channels.MOD_LOGS).sendFile(b.toString().getBytes(), name).queue(success -> {
				//					channel.delete().queue();
				//				});;

			}
			catch(Exception e) {
				e.printStackTrace();
			}


		});

		//channel.delete().queue();
	}

	private static String toStringMessage(Message m) {
		StringBuilder b = new StringBuilder();
		for(MessageEmbed em : m.getEmbeds()) {

			b.append("-------- [ EMBED ] --------\n");

			b.append("Title: " + (em.getTitle() != null ? em.getTitle() : "null") + "\n");
			b.append("Description: " + (em.getDescription() != null ? em.getDescription().replace("\n", "<new line>") : "null") + "\n");
			b.append("Url: " + (em.getUrl() != null ? em.getUrl() : "null") + "\n");

			if(em.getImage() != null) {
				b.append("Image-Url: " + (em.getImage().getUrl() != null ? em.getImage().getUrl() : "null") + "\n");
				b.append("Image-Url Proxy: " + (em.getImage().getProxyUrl() != null ? em.getImage().getProxyUrl() : "null") + "\n");
			}

			if(em.getVideoInfo() != null) {
				b.append("Video-Url: " + (em.getVideoInfo().getUrl() != null ? em.getVideoInfo().getUrl() : "null") + "\n");
			}


			b.append("Color: " + (em.getColor() != null ? Arrays.toString(em.getColor().getComponents(null)) : "no color") + "\n");

			if(em.getAuthor() != null) {
				b.append("Author-Name: " + (em.getAuthor().getName() != null ? em.getAuthor().getName() : "null") + "\n");
				b.append("Author-Url: " + (em.getAuthor().getUrl() != null ? em.getAuthor().getUrl() : "null") + "\n");
				b.append("Author-Url-Icon: " + (em.getAuthor().getIconUrl() != null ? em.getAuthor().getIconUrl() : "null") + "\n");
				b.append("Author-Url-Icon Proxy: " + (em.getAuthor().getProxyIconUrl() != null ? em.getAuthor().getProxyIconUrl() : "null") + "\n");
			}

			if(em.getFooter() != null) {
				b.append("Footer-Text: " + (em.getFooter().getText() != null ? em.getFooter().getText() : "null") + "\n");
				b.append("Footer-Image-Url: " + (em.getFooter().getIconUrl() != null ? em.getFooter().getIconUrl() : "null") + "\n");
				b.append("Footer-Image-Url Proxy: " + (em.getFooter().getProxyIconUrl() != null ? em.getFooter().getProxyIconUrl() : "null") + "\n");
			}

			if(em.getSiteProvider() != null) {
				b.append("SiteProvider-Name: " + (em.getSiteProvider().getName() != null ? em.getSiteProvider().getName() : "null") + "\n");
				b.append("SiteProvider-Url: " + (em.getSiteProvider().getUrl() != null ? em.getSiteProvider().getUrl() : "null") + "\n");
			}

			if(!em.getFields().isEmpty()) {
				b.append("Fields:\n");
				for(Field f : em.getFields()) {
					b.append("	" + f.getName() + " : " + f.getValue() + "\n");
				}
			}

			b.append("-------- [ EMBED ] --------\n");

		}

		if(m.getType() == MessageType.DEFAULT) {
			b.append(m.getAuthor().getAsTag() + " -> " + m.getContentRaw());
		}
		else {
			b.append(m.getContentRaw() + "\n");
		}


		return b.toString();

	}

	private static String getChannelName(Member member) {
		return "t-" + member.getEffectiveName().toLowerCase().replace(" ", "-");
	}



	private static void getAllMessages(TextChannel tc, Consumer<List<Message>> messageConsumer) {
		HistoryCallback callback = new HistoryCallback(tc, messageConsumer);
		tc.getHistoryFromBeginning(100).queue(callback);	
	}

	public static class TicketReactionRoleListener extends ListenerAdapter {

		@Override
		public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
		{

			Guild g = event.getGuild();
			Member member = event.getMember();
			TextChannel channel = event.getChannel();

			/*
			 * SO the bot reacts, triggering the channel to be instantly deleted once created.
			 * Easy mistake to make, took me a while to figure it out.
			 * Whoops.
			 */

			ReactionEmote emote = event.getReactionEmote();

			if(member.getUser().isBot() || member.getUser().isFake()) {
				return;
			}

			if(channel.getIdLong() == Channels.TICKETS) {

				if(emote.isEmoji() && emote.getEmoji().equals(SSEmojis.TICKET)) {

					CommandTicket.tryNewTicket(event.getMember(), event.getChannel());

				} 
				event.getReaction().removeReaction(member.getUser()).queue();

			}
			else if(channel.getName().startsWith("t-")) {

				String channelNameCompare = channel.getName().substring(2);
				if((ModCommand.isModerator(member) || channelNameCompare.equalsIgnoreCase(member.getEffectiveName().replace(" ", "-"))) && emote.getEmoji().equals(SSEmojis.X)) {



					//can delete
					//We want to close the channel their in, not there own ticket that may or may not exist.
					closeTicket(g, event.getChannel(), member);
				}
				else {
					event.getReaction().removeReaction(member.getUser()).queue();
				}
			}

		}

		//Modified from code SLL#5314 sent me
		@Override
		public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
			TextChannel tc = event.getChannel();
			Message m = event.getMessage();
			User user = event.getAuthor();

			if(tc.getName().startsWith("t-") && user.equals(Main.getJda().getSelfUser())) {
				if(m.getType() == MessageType.CHANNEL_PINNED_ADD) {
					m.delete().queue();
				}
			}
		}

	}

}
