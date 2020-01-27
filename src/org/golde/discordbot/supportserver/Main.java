package org.golde.discordbot.supportserver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.golde.discordbot.supportserver.command.BaseCommand;
import org.golde.discordbot.supportserver.command.everyone.CommandHelp;
import org.golde.discordbot.supportserver.command.everyone.CommandPing;
import org.golde.discordbot.supportserver.command.everyone.CommandRPS;
import org.golde.discordbot.supportserver.command.mod.CommandBan;
import org.golde.discordbot.supportserver.command.mod.CommandCommonError;
import org.golde.discordbot.supportserver.command.mod.CommandKick;
import org.golde.discordbot.supportserver.command.mod.CommandLock;
import org.golde.discordbot.supportserver.command.mod.CommandMute;
import org.golde.discordbot.supportserver.command.mod.CommandPruneChat;
import org.golde.discordbot.supportserver.command.mod.CommandUnlock;
import org.golde.discordbot.supportserver.command.mod.CommandUnmute;
import org.golde.discordbot.supportserver.command.owner.CommandAddReaction;
import org.golde.discordbot.supportserver.command.owner.CommandFunnySpongeBob;
import org.golde.discordbot.supportserver.command.owner.CommandTest;
import org.golde.discordbot.supportserver.event.IPGrabberPrevention;
import org.golde.discordbot.supportserver.event.LockdownKicker;
import org.golde.discordbot.supportserver.event.PlayerCounter;
import org.golde.discordbot.supportserver.event.ReactionRolesListener;
import org.golde.discordbot.supportserver.event.WebhookListener;
import org.golde.discordbot.supportserver.event.WhatIsMyPrefix;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main {

	private static JDA jda;

	public static final boolean MAINTANCE = isEclipse();
	
	private static Guild guild;

	public static void main(String[] args) throws Exception {
		// config.txt contains two lines
		List<String> list = Files.readAllLines(Paths.get("config.txt"));

		// the first is the bot token
		String token = list.get(0);

		// the second is the bot's owner's id
		String ownerId = list.get(1);

		// define an eventwaiter, dont forget to add this to the JDABuilder!
		EventWaiter waiter = new EventWaiter();

		// define a command client
		CommandClientBuilder client = new CommandClientBuilder();

		client.useHelpBuilder(false);

		// sets the owner of the bot
		client.setOwnerId(ownerId);

		// sets emojis used throughout the bot on successes, warnings, and failures
		client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");

		// sets the bot prefix
		client.setPrefix(BaseCommand.PREFIX);

		if(MAINTANCE) {
			client.setActivity(Activity.listening("Maintenance by Miranda Cosgrove"));
		}
		else {
			client.setActivity(Activity.watching("Over Eric's Server"));
		}


		//client.

		// adds commands
		client.addCommands(

				new CommandHelp(),
				new CommandPing(),
				new CommandRPS(),

				new CommandKick(),
				new CommandBan(),
				new CommandMute(),
				new CommandUnmute(),
				new CommandPruneChat(),
				new CommandCommonError(),
				new CommandLock(),
				new CommandUnlock(),

				new CommandAddReaction(),
				new CommandFunnySpongeBob(),
				new CommandTest(waiter)

				);

		// start getting a bot account set up
		jda = new JDABuilder(AccountType.BOT)
				// set the token
				.setToken(token)

				// set the game for when the bot is loading
				.setStatus(OnlineStatus.DO_NOT_DISTURB)
				.setActivity(Activity.playing("Loading..."))
				// add the listeners
				.addEventListeners(waiter, client.build())
				.addEventListeners(new IPGrabberPrevention())
				//.addEventListeners(new WatchYourProfanity())
				.addEventListeners(new WhatIsMyPrefix())
				.addEventListeners(new PlayerCounter())
				.addEventListeners(new WebhookListener())
				.addEventListeners(new ReactionRolesListener())
				.addEventListeners(new LockdownKicker())

				.addEventListeners(new ListenerAdapter() {

					@Override
					public void onReady(ReadyEvent event) {
						if(MAINTANCE) {
							event.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
						}
						
						guild = event.getJDA().getGuilds().get(0); //only one guild

					}

				})
				// start it up!
				.build();
	}

	public static JDA getJda() {
		return jda;
	}

	private static boolean isEclipse() {
		return System.getProperty("java.class.path").toLowerCase().contains("eclipse");
	}
	
	public static Guild getGuild() {
		return guild;
	}

}
