package org.golde.discordbot.shared;

import java.util.ArrayList;
import java.util.List;

import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.chatmod.CommandPing;
import org.golde.discordbot.shared.command.everyone.CommandHelp;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.CommandReload;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.shared.util.FileUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public abstract class ESSBot {

	private JDA jda;

	private Guild guild;

	private static final long OWNER_ID = 199652118100049921L;
	
	private EventWaiter waiter;
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public void run() throws Exception {
		
		String token = FileUtil.readGenericConfig("config", false).get(0);

		// define an eventwaiter, dont forget to add this to the JDABuilder!
		waiter = new EventWaiter();

		// define a command client
		CommandClientBuilder client = new CommandClientBuilder();

		client.useHelpBuilder(false);

		// sets the owner of the bot
		client.setOwnerId(String.valueOf(OWNER_ID));

		// sets emojis used throughout the bot on successes, warnings, and failures
		client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");

		// sets the bot prefix
		client.setPrefix(getPrefix());
		
		//Everyone
		List<EveryoneCommand> everyoneCommands = new ArrayList<EveryoneCommand>();
		everyoneCommands.add(new CommandHelp(this));
		
		registerEveryoneCommand(everyoneCommands);
		for(EveryoneCommand cmd : everyoneCommands) {
			client.addCommand(cmd);
		}
		
		//Chat Mod
		List<ChatModCommand> chatModCommand = new ArrayList<ChatModCommand>();
		chatModCommand.add(new CommandPing(this));
		registerChatModCommand(chatModCommand);
		for(ChatModCommand cmd : chatModCommand) {
			client.addCommand(cmd);
		}
		
		//Guild Mod
		List<GuildModCommand> guildModCommands = new ArrayList<GuildModCommand>();
		registerGuildModCommand(guildModCommands);
		for(GuildModCommand cmd : guildModCommands) {
			client.addCommand(cmd);
		}
		
		//Owner
		List<OwnerCommand> ownerCommands = new ArrayList<OwnerCommand>();
		ownerCommands.add(new CommandReload(this));
		registerOwnerCommand(ownerCommands);
		for(OwnerCommand cmd : ownerCommands) {
			client.addCommand(cmd);
		}

		// start getting a bot account set up
		JDABuilder builder = JDABuilder.createDefault(token, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_PRESENCES)

				.setMemberCachePolicy(MemberCachePolicy.ALL)
				// set the game for when the bot is loading
				.setStatus(OnlineStatus.DO_NOT_DISTURB)
				.setActivity(Activity.playing("Loading..."))
				// add the listeners
				.addEventListeners(waiter, client.build())

				.addEventListeners(new ListenerAdapter() {

					@Override
					public void onReady(ReadyEvent event) {

						jda.getPresence().setActivity(Activity.listening(getPrefix() + "help"));
						guild = event.getJDA().getGuilds().get(0); //only one guild
						ESSBot.this.onReady();
					}

				});

		List<EventBase> eventList = new ArrayList<EventBase>();
		registerEventListeners(eventList);
		for(EventBase listener : eventList) {
			builder.addEventListeners(listener);
		}
		onLoad();
		// start it up!
		jda = builder.build();
	}

	public abstract String getPrefix();
	public abstract void onReady();
	public abstract void onLoad();
	public abstract void onReload();
	public abstract void registerEventListeners(List<EventBase> events);
	public abstract void registerEveryoneCommand(List<EveryoneCommand> cmds);
	public abstract void registerChatModCommand(List<ChatModCommand> cmds);
	public abstract void registerGuildModCommand(List<GuildModCommand> cmds);
	public abstract void registerOwnerCommand(List<OwnerCommand> cmds);
	

	public final JDA getJda() {
		return jda;
	}

	public final Guild getGuild() {
		return guild;
	}

	public static final long getOwnerId() {
		return OWNER_ID;
	}
	
	public final EventWaiter getWaiter() {
		return waiter;
	}

}

