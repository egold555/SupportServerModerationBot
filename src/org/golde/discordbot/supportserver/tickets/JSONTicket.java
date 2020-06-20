package org.golde.discordbot.supportserver.tickets;

import java.util.List;

import org.golde.discordbot.supportserver.Main;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public class JSONTicket {

	final List<JSONMessage> messages;
	final long owner;
	final long channel;
	final List<Long> members;
	final String channelName;
	
	public JSONTicket(Ticket ticket) {
		messages = ticket.messages;
		owner = ticket.owner.getIdLong();
		channel = ticket.channel.getIdLong();
		this.members = ticket.members;
		this.channelName = ticket.channel.getName();
	}
	
	public Ticket toTicket(String fileName) {
		Guild g = Main.getGuild();
		Ticket ticket = new Ticket();
		ticket.channel = g.getTextChannelById(channel);
		ticket.owner = g.getMemberById(owner);
		ticket.members = members;
		ticket.messages = messages;
		ticket.fileName = "tickets/" + fileName.replace(".json", "");
		return ticket;
	}
	
}
