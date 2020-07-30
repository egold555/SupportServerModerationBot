package org.golde.discordbot.tickets.tickets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.BaseCommand;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.command.support.SupportCommand;
import org.golde.discordbot.shared.constants.Categories;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.supportserver.database.Database;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketManager {

	static List<Ticket> openTickets = new ArrayList<Ticket>();
	private static final int DELETE_TIME = 5;

	//	public static void create(Member member, TextChannel cmdChannel) {
	//
	//		long ownerId = member.getIdLong();
	//
	//		if(openTickets.containsKey(ownerId)) {
	//			//ticket already exists
	//			BaseCommand.replySuccess(cmdChannel, "You already have a ticket! You can only have one ticket.", 10);
	//		}
	//		else {
	//			//create the ticket
	//			Ticket ticket = new Ticket(member);
	//			openTickets.put(ownerId, ticket);
	//
	//
	//			ticket.create(onSuccess -> {
	//				EmbedBuilder eb = new EmbedBuilder();
	//				eb.setColor(Main.EMBED_COLOR);
	//				eb.setTitle("Welcome to your new ticket!");
	//				eb.setDescription("Hello " + member.getEffectiveName() + "! This is your new ticket. When you are finished, please click the X reaction to close, or type ```" + BaseCommand.PREFIX + "ticket close``` Only you and Moderators can close tickets. If you ever need to get back to this message, I have pinned it.");
	//				eb.setTimestamp(Instant.now());
	//				eb.setFooter(Main.getJda().getSelfUser().getAsTag(), Main.getJda().getSelfUser().getAvatarUrl());
	//
	//				//BaseCommand.replySuccess(cmdChannel, "Your ticket has been created! Click " + onSuccess.getAsMention() + " to quickly jump there.", DELETE_TIME);
	//				onSuccess.sendMessage(eb.build()).queue(success -> {
	//					success.addReaction(SSEmojis.X).queue();
	//					onSuccess.pinMessageById(success.getId()).queue();
	//				});
	//
	//				//BaseCommand.reply(onSuccess, "Hey " + member.getAsMention() + "! Over here!", 2);
	//			});
	//
	//		}
	//
	//	}

	public static boolean alreadyHadTicket(Member member) {
		for(Ticket t : openTickets) {
			if(t.getOwner().getIdLong() == member.getIdLong()) {
				return true;
			}
		}
		return false;
	}

	public static void loadFromFile(ESSBot bot) {
		openTickets.clear();
		for (File file : new File("res/tickets").listFiles()) {
			if (file.isFile()) {
				try {
					JSONTicket jsonTicket = Database.jsonFile2JavaObject(file, JSONTicket.class);
					openTickets.add(jsonTicket.toTicket(bot, file.getName()));
					System.out.println("Loaded ticket from file: " + file.getAbsolutePath());
				}
				catch(Exception e) {
					System.err.println("Failed to load ticket: " + file.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}

	}

	public static void create(ESSBot bot, Member member, QuestionStateAbstract state) {
		long ownerId = member.getIdLong();

		//create the ticket
		Ticket ticket = new Ticket(member);
		openTickets.add(ticket);


		ticket.create(state, onSuccess -> {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(bot.EMBED_COLOR);
			eb.setTitle("Welcome to your new ticket!");
			eb.setDescription("Hello " + member.getEffectiveName() + "! "
					+ "Thank you for taking the time to answer "
					+ "\n\n__The following commands are available to you inside your ticket:__\n"
					+ "**" + bot.getPrefix() + "ticket close** - close your ticket\n"
					+ "**" + bot.getPrefix() + "ticket add <user>** - Add a user to your ticket\n"
					+ "**" + bot.getPrefix() + "ticket remove <user>** - remove a user from your ticket\n"
					+ "**" + bot.getPrefix() + "ticket public** - Makes your ticket publically viewable by everyone\n"
					+ "**" + bot.getPrefix() + "ticket private** - Undoes `ticket public` and makes your ticket private again\n"
					+ "\n__Reaction Emojis:__\n"
					+ ":x: Close the ticket\n" 
					+ ":unlock: Open the ticket to the public\n"
					+ ":lock: Make the ticket private\n"
					+ "\nWhen you are finished, please close your ticket " + EmojiManager.getForAlias(":slight_smile:").getUnicode()
					+ "\n\nWhen you close your ticket, it will be archived, and a copy of your ticket will be sent to you, and any other participating members in DM's by " + Main.getJda().getSelfUser().getAsMention() + ". This may take some time depending on the size of your ticket.");
			eb.setTimestamp(Instant.now());
			eb.setFooter(bot.getJda().getSelfUser().getAsTag(), bot.getJda().getSelfUser().getAvatarUrl());

			//BaseCommand.replySuccess(state.getChannel(), "Your ticket has been created! Click " + onSuccess.getAsMention() + " to quickly jump there., DELETE_TIME");
			onSuccess.sendMessage(eb.build()).queue(success -> {
				success.addReaction(SSEmojis.X).queue(emoji1 -> {
					success.addReaction(SSEmojis.UNLOCK).queue(emoji2 -> {
						success.addReaction(SSEmojis.LOCK).queue();
					});
				});

				onSuccess.pinMessageById(success.getId()).queue();
			});

		});

	}

	public static void addMember(Member sender, TextChannel cmdchannel, List<Member> mentionedMembers) {
		for(Member personToAdd : mentionedMembers) {
			addMember(sender, cmdchannel, personToAdd);
		}
	}

	public static void addMember(ESSBot bot, Member sender, TextChannel cmdchannel, Member personToAdd) {

		Ticket ticket = getTicketFromChannel(cmdchannel);

		if(ticket == null) {
			BaseCommand.replyError(cmdchannel, cmdchannel.getAsMention() + " is not a valid ticket channel!", DELETE_TIME);
			return;
		}

		if(!hasPermission(sender, ticket)) {
			BaseCommand.replyError(cmdchannel, "No permission :(", DELETE_TIME);
			return;
		}

		ticket.add(personToAdd);
		BaseCommand.replySuccess(cmdchannel, "Added " + personToAdd.getEffectiveName() + " to this ticket!");
	}

	public static void removeMember(Member sender, TextChannel cmdchannel, List<Member> mentionedMembers) {
		for(Member personToAdd : mentionedMembers) {
			removeMember(sender, cmdchannel, personToAdd);
		}
	}

	public static void removeMember(Member sender, TextChannel cmdchannel, Member personToAdd) {

		Ticket ticket = getTicketFromChannel(cmdchannel);

		if(ticket == null) {
			BaseCommand.replyError(cmdchannel, cmdchannel.getAsMention() + " is not a valid ticket channel!", DELETE_TIME);
			return;
		}

		if(!hasPermission(sender, ticket)) {
			BaseCommand.replyError(cmdchannel, "No permission :(", DELETE_TIME);
			return;
		}

		ticket.remove(personToAdd);
		BaseCommand.replySuccess(cmdchannel, "Removed " + personToAdd.getEffectiveName() + " from this ticket!");

	}

	public static void makePublic(Member sender, TextChannel tc) {
		Ticket ticket = getTicketFromChannel(tc);

		if(ticket == null) {
			BaseCommand.replyError(tc, tc.getAsMention() + " is not a valid ticket channel!", DELETE_TIME);
			return;
		}

		if(!hasPermission(sender, ticket)) {
			BaseCommand.replyError(tc, "No permission :(", DELETE_TIME);
			return;
		}

		ticket.makePublic();

	}

	public static void makePrivate(Member sender, TextChannel tc) {
		Ticket ticket = getTicketFromChannel(tc);

		if(ticket == null) {
			BaseCommand.replyError(tc, tc.getAsMention() + " is not a valid ticket channel!", DELETE_TIME);
			return;
		}

		if(!hasPermission(sender, ticket)) {
			BaseCommand.replyError(tc, "No permission :(", DELETE_TIME);
			return;
		}

		ticket.makePrivate();
	}

	@SuppressWarnings("resource")
	public static boolean close(Member sender, TextChannel cmdChannel) {
		Ticket ticket = getTicketFromChannel(cmdChannel);

		if(ticket == null) {
			BaseCommand.replyError(cmdChannel, cmdChannel.getAsMention() + " is not a valid ticket channel!", DELETE_TIME);
			return false;
		}

		if(!hasPermission(sender, ticket)) {
			BaseCommand.replyError(cmdChannel, "No permission :(", DELETE_TIME);
			return false;
		}

		ticket.close(onFinish -> {
			//archive and delete
			ticket.getChannel().sendMessage("Archiving and deleting channel....").queue();
			openTickets.remove(ticket);

			Guild g = cmdChannel.getGuild();

			BaseCommand.replySuccess(g.getTextChannelById(Channels.TICKET_LOGS), "Ticket closed", ticket.getChannel().getName());

			try {
				g.getTextChannelById(Channels.TICKET_LOGS).sendFile(ticket.getFile()).queueAfter(3, TimeUnit.SECONDS);
			}
			catch(Exception e) {
				String newName = UUID.randomUUID().toString() + ".json";
				System.err.println("Failed to archive this ticket in Discord. Archiving it on system disc for the time being. " + newName);
				FileChannel sourceChannel = null;
				FileChannel destChannel = null;
				try {
					sourceChannel = new FileInputStream(ticket.getFile()).getChannel();
					destChannel = new FileOutputStream(new File("res/tickets/archive/" + newName)).getChannel();
					destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
				} 
				catch (IOException e1) {
					System.err.println("Well I failed to archive this ticket even on system disc. Its done now forever sorry about that!");
					e1.printStackTrace();
				}
				finally{
					try {
						sourceChannel.close();
						destChannel.close();
					} 
					catch (IOException ignored) {
						//Well fuck lets just hope we don't get here okay?
					}
					
				}
				
			}

			for(long mem : ticket.getMembers()) {
				User u = g.getMemberById(mem).getUser();

				try {
					if(!u.isBot() && !u.isFake()) {
						u.openPrivateChannel().queue(privateChannelSuccess -> {
							privateChannelSuccess.sendMessage("Hello! This feature is still in beta. Eventually you will get a beautiful HTML file that looks like it was a screenshot of your ticket channel. \n\n For the time being though, this JSON file will need to sufice until I roll out the HTML file generation.").addFile(ticket.getFile()).queue();
						});
					}
				}
				catch(Exception ignored) {
					System.err.println("Failed to send a file to ");
					ignored.printStackTrace();
				}

			}

			final String ticketName = ticket.getChannel().getName();

			ticket.getChannel().getManager().setParent(ticket.getChannel().getGuild().getCategoryById(Categories.TEMPORARY_CHANNELS)).queue();
			ticket.getChannel().delete().queueAfter(30, TimeUnit.SECONDS, onDelete -> {

				System.out.println("Deleted Ticket File: " + ticketName + " - " + ticket.getFile().getAbsolutePath());
				ticket.getFile().delete();


			});



		});

		return true;

	}

	public static void genHtml(TextChannel textChannel) {
		Ticket ticket = getTicketFromChannel(textChannel);
		if(ticket == null) {
			return;
		}

		String html = JSONToHTMLConverter.convert(new JSONTicket(ticket));

		textChannel.sendFile(html.getBytes(), "exported.html").queue();;

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static boolean hasPermission(Member sender, Ticket ticket) {
		if(GuildModCommand.isModerator(sender) || SupportCommand.isModerator(sender) || OwnerCommand.isOwner(sender)) {
			return true;
		}

		return sender.getIdLong() == ticket.getOwner().getIdLong();
	}

	private static Ticket getTicketFromChannel(TextChannel tc) {
		for(Ticket ticket : openTickets) {
			if(ticket.getChannel().getIdLong() == tc.getIdLong()) {
				return ticket;
			}
		}
		return null;
	}
	
	public static boolean isInTicket(TextChannel tc){
		Ticket t = getTicketFromChannel(tc);
		return (t != null);
	}

	public static class TicketManagerEvents extends EventBase {



		public TicketManagerEvents(ESSBot bot) {
			super(bot);
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

			if(tc.getName().startsWith("t-")) {
				if(m.getType() == MessageType.DEFAULT) {
					Ticket tick = getTicketFromChannel(tc);
					//in the middle of deleting chanel
					if(tick != null) {
						getTicketFromChannel(tc).addMessage(m);
					}

				}
			}
		}

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

			//System.out.println(emote.getEmoji() + " " + emote.getAsCodepoints());

			if(member.getUser().isBot() || member.getUser().isFake()) {
				return;
			}

			if(channel.getName().startsWith("t-")) {



				//X close emoji
				if(emote.getEmoji().equals(SSEmojis.X)) {
					boolean success = TicketManager.close(member, event.getChannel());
					if(!success) {
						event.getReaction().removeReaction(member.getUser()).queue();;
					}
				}

				//unlock
				else if(emote.getEmoji().equals(SSEmojis.UNLOCK)) {
					event.getReaction().removeReaction(member.getUser()).queue();;
					TicketManager.makePublic(member, channel);
				}

				//lock
				else if(emote.getEmoji().equals(SSEmojis.LOCK)) {
					event.getReaction().removeReaction(member.getUser()).queue();;
					TicketManager.makePrivate(member, channel);
				}

			}

		}
	}


}
