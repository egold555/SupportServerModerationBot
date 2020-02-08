package org.golde.discordbot.supportserver.command.owner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.UsernameCache;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;

public class DumpUsernameCache extends OwnerCommand {

	public DumpUsernameCache() {
		this.name = "dumpUsernameCache";
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		try {
			File exportFile = new File("res/username-cache.json");
			FileWriter writer = new FileWriter(exportFile);
			
			List<UsernameCache> list = new ArrayList<UsernameCache>();
			
			for(Member m : Main.getGuild().getMembers()) {
				
				UsernameCache cache = new UsernameCache(m.getIdLong());
				cache.setUsername(m.getUser().getAsTag());
				list.add(cache);
			}
			
			writer.append(Database.GSON.toJson(list));
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
