package org.golde.discordbot.supportserver.command.owner;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.constants.Channels;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.JsonEmbed;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.database.SimpleUser;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.entities.ReceivedMessage;

public class CommandDumpModLog extends OwnerCommand {

	public CommandDumpModLog() {
		this.name = "dumpModLog";
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		TextChannel tc = event.getGuild().getTextChannelById(Channels.MOD_LOGS);

		tc.getHistoryFromBeginning(100).queue(history ->
		{

			try {
				File exportFile = new File("res/export.txt");
				FileWriter writer = new FileWriter(exportFile);
				List<JsonEmbed> msgJson = new ArrayList<JsonEmbed>();

				for(Message m : history.getRetrievedHistory()) {

					if(m instanceof ReceivedMessage) {
						ReceivedMessage rm = (ReceivedMessage)m;

						
						
						if(rm.getEmbeds().size() > 0) {
							MessageEmbed em = rm.getEmbeds().get(0);

							JsonEmbed jsonEmbed = new JsonEmbed();

							boolean correct = false;
							
							for(Field f : em.getFields()) {
								
								if(f.getName().equalsIgnoreCase("Action:")) {
									System.err.println(f.getValue());
									ModAction act = ModAction.valueOf(f.getValue());
									if(act == ModAction.BAN || act == ModAction.KICK || act == ModAction.MUTE || act == ModAction.UNMUTE || act == ModAction.WARN) {
										jsonEmbed.setAction(act.name());
										jsonEmbed.setTimestamp(rm.getTimeCreated().toInstant().toEpochMilli());
										correct = true;
									}
								}
								
								if(f.getName().equalsIgnoreCase("Moderator:") && correct) {
									jsonEmbed.setModerator(f.getValue().replace("\u003c@", "").replace("\u003e", ""));
								}
								if(f.getName().equalsIgnoreCase("Offender:") && correct) {
									jsonEmbed.setOffender(f.getValue().replace("\u003c@", "").replace("\u003e", ""));
								}
								if(f.getName().equalsIgnoreCase("Reason:") && correct) {
									jsonEmbed.setReason(f.getValue());
								}
								

							}
							
							if(correct) {
								msgJson.add(jsonEmbed);
							}
							
						}


					}
				}
				
				List<SimpleUser> users = new ArrayList<SimpleUser>();
				
				for(JsonEmbed em : msgJson) {
					long offender = Long.parseLong(em.getOffender());
					SimpleUser user = Database.getUser(offender);
					
					if(Main.getGuild().getMemberById(offender) != null && Main.getGuild().getMemberById(offender).getUser() != null) {
						user.setUserLKU(Main.getGuild().getMemberById(offender).getUser().getAsTag());
					}
					else {
						user.setUserLKU("null");
					}
					
					user.addOffence(new Offence(ModAction.valueOf(em.getAction()), offender, Main.getGuild().getMemberById(em.getModerator()).getUser().getAsTag(), em.getReason(), em.getTimestamp()));
					if(!users.contains(user)) {
						users.add(user);
					}
				}
				
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				writer.append(gson.toJson(users));

				writer.flush();
				writer.close();
				event.replySuccess("Successfully dumped to file!");
			}
			catch(Exception e) {
				e.printStackTrace();
			}


		});
	}

}
