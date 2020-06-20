package org.golde.discordbot.supportserver.tickets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.command.BaseCommand;
import org.golde.discordbot.supportserver.constants.Categories;
import org.golde.discordbot.supportserver.constants.Roles;
import org.golde.discordbot.supportserver.database.Database;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;

public class Ticket {

	private final Guild g;
	Member owner;
	TextChannel channel;
	List<JSONMessage> messages = new ArrayList<JSONMessage>();
	String fileName;
	List<Long> members = new ArrayList<Long>();

	@Deprecated
	public Ticket() {
		g = Main.getGuild();
	}
	
	public Ticket(Member owner) {
		this.g = owner.getGuild();
		this.owner = owner;
	}

	public void create(Consumer<TextChannel> onCreated) {
		g.getCategoryById(Categories.TICKETS).createTextChannel(getTextChannelName(owner)).queue(success -> {
			internal_create(success, onCreated);
		});
	}
	
	public void create(QuestionStateAbstract state, Consumer<TextChannel> onCreated) {
		
		for(Message m : state.getResponses()) {
			messages.add(new JSONMessage(m));
		}
		
		state.getChannel().getManager().setParent(g.getCategoryById(Categories.TICKETS)).queue(successMove -> {
			
			state.getChannel().getManager().setName(getTextChannelName(owner)).queue();
			internal_create(state.getChannel(), onCreated);
			
		});
	}
	
	private void internal_create(TextChannel chan, Consumer<TextChannel> onCreated) {
		channel = chan;
		add(g.getRoleById(Roles.TICKET_SUPPORT_TEAM));
		onCreated.accept(chan);
		fileName = "tickets/" + getTextChannelName(owner);
		
		Database.saveToFile(new JSONTicket(this), fileName);
		
	}

	public void add(IPermissionHolder holder) {
		channel.putPermissionOverride(holder).queue(onSuccess1 -> {
			onSuccess1.getManager().setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_ATTACH_FILES).queue();
		});
		
	}

	public void remove(IPermissionHolder holder) {

		PermissionOverride override = channel.getPermissionOverride(holder);
		
		if(override != null) {
			override.delete().queue();
			//System.out.println("Removing override for " + holder.getId());
		}
	}
	
	public void deny(IPermissionHolder holder) {
		channel.putPermissionOverride(holder).queue(onSuccess1 -> {
			onSuccess1.getManager().setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_ATTACH_FILES).queue();
		});
	}

	public void close(Consumer<Void> onFinish) {
		deny(owner);
		deny(g.getRoleById(Roles.TICKET_SUPPORT_TEAM));
		deny(g.getRoleById(Roles.EVERYONE));
		
		for(long member : members) {
			deny(g.getMemberById(member));
		}
		
		onFinish.accept(null);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static String getTextChannelName(Member member) {
		return  "t-" + member.getUser().getName().toLowerCase().replace(" ", "-").replaceAll("[^a-zA-Z0-9]", "-");
	}

	public Member getOwner() {
		return owner;
	}

	protected void addMessage(Message m) {
		messages.add(new JSONMessage(m));
		if(!members.contains(m.getMember().getIdLong())) {
			members.add(m.getMember().getIdLong());
		}
		Database.saveToFile(new JSONTicket(this), fileName);
	}
	
	public TextChannel getChannel() {
		return channel;
	}
	
	public List<Long> getMembers() {
		return members;
	}

	public File getFile() {
		return new File("res/" + fileName + ".json");
	}

	public void makePublic() {
		channel.putPermissionOverride(g.getRoleById(Roles.EVERYONE)).queue(onSuccess1 -> {
			onSuccess1.getManager().setAllow(Permission.MESSAGE_READ).queue();
			BaseCommand.replySuccess(getChannel(), "Channel set to **public**. Everyone can view it now. \n\nPeople can not speak in this channel until you add them. \n\nTo add a person, use **" + BaseCommand.PREFIX + "ticket add <@user>**");
		});
		
	}
	
	public void makePrivate() {
		channel.putPermissionOverride(g.getRoleById(Roles.EVERYONE)).queue(onSuccess1 -> {
			onSuccess1.getManager().setDeny(Permission.MESSAGE_READ).queue();
			BaseCommand.replySuccess(getChannel(), "Channel set to **private** Only added members, and Ticket Support staff can read your ticket.");
		});
	}
	
}
