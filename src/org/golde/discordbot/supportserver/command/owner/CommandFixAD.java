package org.golde.discordbot.supportserver.command.owner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;

import org.golde.discordbot.supportserver.util.StringUtil;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;

public class CommandFixAD extends OwnerCommandDangerous {

	private static final String KILL = "sudo killall -9 anydesk";
	private static final String START = "anydesk </dev/null &>/dev/null &";

	public CommandFixAD() {
		super("fixAD", null, "This is a bad idea. But hey its a fix right?");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();

		tc.sendMessage("Trying to kill AD...").queue(onSuccessSendUpdateMessage -> {
			runCmd(KILL, successKill -> {
				tc.sendMessage(successKill).queue(successSendKill -> {
					runCmd(START, successStart -> {
						tc.sendMessage(successStart).queue();
					});
				});
			});
		});

	}

	static void runCmd(String cmd, Consumer<String> feedback) {
		try {
			ProcessBuilder builder = new ProcessBuilder(cmd.split("\\s+"));
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while (true) {
				line = r.readLine();
				if (line == null) { break; }
				
			}
			if(line == null || line.isEmpty()) {
				line = "No response from console, but program executed.";
			}
			feedback.accept(line);
		}
		catch(IOException e) {
			try {
				feedback.accept(StringUtil.toStringException(e));
			} catch (IOException e1) {
				feedback.accept("You should never see this");
			}
		}

	}

}
