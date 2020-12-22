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
import org.golde.discordbot.shared.db.AbstractDBTranslation;
import org.golde.discordbot.shared.db.FileUtil;
import org.golde.discordbot.shared.db.ICanHasDatabaseFile;
import org.golde.discordbot.shared.event.EventBase;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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

/**
 * Abstract class defining a bot for my support server.
 * All bots must implement this class.
 * @author Eric Golde
 *
 */
public abstract class ESSBot {

	//JDA instance
	private JDA jda;

	//Shortcut to the ESS server
	private Guild guild;

	//My user ID
	private static final long OWNER_ID = 199652118100049921L;

	//Used for the command system
	private EventWaiter waiter;

	//GSON instance for everything that could possably use GSON
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();

	//List of database callbacks we need to initalize
	private List<ICanHasDatabaseFile> databaseCallbacks = new ArrayList<ICanHasDatabaseFile>();
	
	//MYSQL server instance. Can be null if we are not using MYSQL
	private SessionFactory sessionFactory;

	/**
	 * The main method that starts the bot instance
	 * @throws Exception
	 */
	public void run() throws Exception {

		//grab the token from the config file. Its always the first line (Yeah this is bad but it works)
		String token = FileUtil.readGenericConfig("config", false).get(0);

		// define an eventwaiter, dont forget to add this to the JDABuilder!
		waiter = new EventWaiter();

		CommandClientBuilder client = null;
		
		//Some bots like the website bot don't have commands, so we don't initalise any commands if the prefix for the bot is null
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
		
		//Register any mysql translations. If no translations exist, we assume the bot doesn't use any MYSQL.
		//Yeah its not the best way but it works for my application
		List<Class<? extends AbstractDBTranslation>> dbTranslations = new ArrayList<Class<? extends AbstractDBTranslation>>();
		registerDatabaseTranslations(dbTranslations);
		if(dbTranslations.size() > 0) {
			//We should initalize the Mysql DB
			System.out.println("Connecting to MYSQL...");
			Configuration cfg = new Configuration();
			
			for(Class<? extends AbstractDBTranslation> translation : dbTranslations) {
				cfg.addAnnotatedClass(translation);
			}
			
			sessionFactory = cfg.configure().buildSessionFactory();
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

						//If we don't have any commands, don't set a activity
						if(getPrefix() != null) {
							jda.getPresence().setActivity(Activity.listening(getPrefix() + "help"));
						}
						
						guild = event.getJDA().getGuilds().get(0); //setup our shortcut guild
						ESSBot.this.onReady();
					}

				});
		
		//Same thing, if we don't have a prefix, dont initalize the command builder event
		if(client != null) {
			builder.addEventListeners(waiter, client.build());
		}

		//Go through every event listener, and register it
		List<EventBase> eventList = new ArrayList<EventBase>();
		registerEventListeners(eventList);
		for(EventBase listener : eventList) {
			builder.addEventListeners(listener);
			tryInterfaceThings(listener);
		}
		
		//start the bot!
		private_onLoad();
		// start it up!
		jda = builder.build();
	}

	//Methods every bot can implment
	public abstract String getPrefix();
	@Deprecated public void onReady() {};
	@Deprecated public void onLoad() {};
	public void onReload() {};
	public void registerEventListeners(List<EventBase> events) {};
	public void registerEveryoneCommand(List<EveryoneCommand> cmds){};
	public void registerChatModCommand(List<ChatModCommand> cmds){};
	public void registerGuildModCommand(List<GuildModCommand> cmds){};
	public void registerOwnerCommand(List<OwnerCommand> cmds){};
	public void registerDatabaseTranslations(List<Class<? extends AbstractDBTranslation>> dbt) {}

	//Kind of a shitty way of doing this but it works 
	private void private_onLoad() {
		onLoad();
		for(ICanHasDatabaseFile ican : databaseCallbacks) {
			ican.loadOnce();
		}
	}

	//Kind of a shitty way of doing this but it works 
	public void private_onReload() {
		onReload();
		for(ICanHasDatabaseFile ican : databaseCallbacks) {
			ican.reload();
		}
	}

	//I might be able to condence this down to one function but for the time being its two functions
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

	//Getters. Would use lombok but I need them to be final in this class
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
	
	public final SessionFactory getMySQL() {
		return sessionFactory;
	}

}

