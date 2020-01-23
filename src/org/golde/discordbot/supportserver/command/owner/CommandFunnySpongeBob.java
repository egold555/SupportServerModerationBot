package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandFunnySpongeBob extends OwnerCommand {

	public CommandFunnySpongeBob() {
		super();
		this.name = "whoLivesInAPineappleUnderTheSea";
		this.help = "This is incase I forget, it happens a lot.";
		this.aliases = new String[] {"wliaputs"};
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		event.reply("▔▔▔▔▔▔▔▔▔▔▔╲\n" + 
				"▕╮╭┻┻╮╭┻┻╮╭▕╮╲\n" + 
				"▕╯┃╭╮┃┃╭╮┃╰▕╯╭▏\n" + 
				"▕╭┻┻┻┛┗┻┻┛    ▕    ╰▏\n" + 
				"▕╰━━━┓┈┈┈╭╮▕╭╮▏\n" + 
				"▕╭╮╰┳┳┳┳╯╰╯▕╰╯▏\n" + 
				"▕╰╯┈┗┛┗┛┈╭╮▕╮┈▏");
		
	}

}
