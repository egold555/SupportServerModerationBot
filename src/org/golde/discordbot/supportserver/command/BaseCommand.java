package org.golde.discordbot.supportserver.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public abstract class BaseCommand extends Command {

	public static final String PREFIX = ";";
	
	protected static final Category CATEGORY_EVERYONE;
	protected static final Category CATEGORY_MODERATOR;
	protected static final Category CATEGORY_OWNER;
	
	static {
		CATEGORY_EVERYONE = new Category("Everyone");
		CATEGORY_MODERATOR = new Category("Moderator");
		CATEGORY_OWNER = new Category("Founder");
	}
	
	public BaseCommand(@Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		
		this.name = nameIn;
		if(argsIn != null) {
			this.arguments = argsIn;
		}
		
		if(helpIn != null) {
			this.help = helpIn;
		}
		
		if(aliasesIn != null && aliasesIn.length > 0) {
			this.aliases = aliasesIn;
		}
		
		
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		List<String> toPass = new ArrayList<String>();
		
		if(event.getArgs() != null && event.getArgs().length() > 0) {
			final String[] split = event.getMessage().getContentRaw().replaceFirst(
	                "(?i)" + Pattern.quote(PREFIX), "").split("\\s+");
			
			toPass = Arrays.asList(split);
		}
		
		//delete their command
		//event.getMessage().delete().queue();
		
		execute(event, toPass);
		
	}
	
	protected abstract void execute(CommandEvent event, List<String> args);
	
	
}
