package org.golde.discordbot.shared;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.golde.discordbot.shared.command.BaseCommand;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.chatmod.CommandPing;
import org.golde.discordbot.shared.command.everyone.CommandHelp;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.command.owner.CommandReload;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.shared.db.ICanHasDatabaseFile;
import org.golde.discordbot.shared.event.EventBase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public abstract class ESSBot {

	private JDA jda;

	private Guild guild;

	private static final long OWNER_ID = 199652118100049921L;

	private EventWaiter waiter;

	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();

	private List<ICanHasDatabaseFile> databaseCallbacks = new ArrayList<ICanHasDatabaseFile>();

	public void run() throws Exception {

		String token = FileUtil.readGenericConfig("config", false).get(0);

		// define an eventwaiter, dont forget to add this to the JDABuilder!
		waiter = new EventWaiter();

		CommandClientBuilder client = null;
		if(getPrefix() != null) {


			// define a command client
			client = new CommandClientBuilder();

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
				tryInterfaceThings(cmd);
			}

			//Chat Mod
			List<ChatModCommand> chatModCommand = new ArrayList<ChatModCommand>();
			chatModCommand.add(new CommandPing(this));
			registerChatModCommand(chatModCommand);
			for(ChatModCommand cmd : chatModCommand) {
				client.addCommand(cmd);
				tryInterfaceThings(cmd);
			}

			//Guild Mod
			List<GuildModCommand> guildModCommands = new ArrayList<GuildModCommand>();
			registerGuildModCommand(guildModCommands);
			for(GuildModCommand cmd : guildModCommands) {
				client.addCommand(cmd);
				tryInterfaceThings(cmd);
			}

			//Owner
			List<OwnerCommand> ownerCommands = new ArrayList<OwnerCommand>();
			ownerCommands.add(new CommandReload(this));
			registerOwnerCommand(ownerCommands);
			for(OwnerCommand cmd : ownerCommands) {
				client.addCommand(cmd);
				tryInterfaceThings(cmd);
			}

		}

		// start getting a bot account set up
		JDABuilder builder = JDABuilder.createDefault(token, EnumSet.allOf(GatewayIntent.class))

				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.enableCache(EnumSet.allOf(CacheFlag.class))
				// set the game for when the bot is loading
				.setStatus(OnlineStatus.DO_NOT_DISTURB)
				.setActivity(Activity.playing("Loading..."))
				

				.addEventListeners(new ListenerAdapter() {

					@Override
					public void onReady(ReadyEvent event) {

						if(getPrefix() != null) {
							jda.getPresence().setActivity(Activity.listening(getPrefix() + "help"));
						}
						
						guild = event.getJDA().getGuilds().get(0); //only one guild
						ESSBot.this.onReady();
					}

				});
		
		if(client != null) {
			builder.addEventListeners(waiter, client.build());
		}

		List<EventBase> eventList = new ArrayList<EventBase>();
		registerEventListeners(eventList);
		for(EventBase listener : eventList) {
			builder.addEventListeners(listener);
			tryInterfaceThings(listener);
		}
		private_onLoad();
		// start it up!
		jda = builder.build();
	}

	public abstract String getPrefix();
	@Deprecated public void onReady() {};
	@Deprecated public void onLoad() {};
	public void onReload() {};
	public void registerEventListeners(List<EventBase> events) {};
	public void registerEveryoneCommand(List<EveryoneCommand> cmds){};
	public void registerChatModCommand(List<ChatModCommand> cmds){};
	public void registerGuildModCommand(List<GuildModCommand> cmds){};
	public void registerOwnerCommand(List<OwnerCommand> cmds){};

	private void private_onLoad() {
		onLoad();
		for(ICanHasDatabaseFile ican : databaseCallbacks) {
			ican.loadOnce();
		}
	}

	public void private_onReload() {
		onReload();
		for(ICanHasDatabaseFile ican : databaseCallbacks) {
			ican.reload();
		}
	}

	private void tryInterfaceThings(EventBase listener) {
		if(listener instanceof ICanHasDatabaseFile) {
			ICanHasDatabaseFile ican = (ICanHasDatabaseFile) listener;
			databaseCallbacks.add(ican);
		}
	}

	private void tryInterfaceThings(BaseCommand cmd) {
		if(cmd instanceof ICanHasDatabaseFile) {
			ICanHasDatabaseFile ican = (ICanHasDatabaseFile) cmd;
			databaseCallbacks.add(ican);
		}
	}

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

