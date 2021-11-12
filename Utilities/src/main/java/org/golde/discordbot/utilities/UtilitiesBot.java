package org.golde.discordbot.utilities;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.utilities.command.chatmod.CommandLookup;
import org.golde.discordbot.utilities.command.everyone.CommandCommonError;
import org.golde.discordbot.utilities.command.owner.CommandAddReaction;
import org.golde.discordbot.utilities.command.owner.CommandYoutube;
import org.golde.discordbot.utilities.commonerror.CommonErrorManager;
import org.golde.discordbot.utilities.crash.CrashReportEventHandler;
import org.golde.discordbot.utilities.db.DB;
import org.golde.discordbot.utilities.event.AutoRemoveBirthdayRole;
import org.golde.discordbot.utilities.event.ImageOnlyChannel;
import org.golde.discordbot.utilities.event.LikeDislikePollEvents;
import org.golde.discordbot.utilities.event.OCRCommonErrors;
import org.golde.discordbot.utilities.event.PlayerCounter;
import org.golde.discordbot.utilities.event.ReactionRolesListener;

public class UtilitiesBot extends ESSBot {

	public static void main(String[] args) throws Exception {
		new UtilitiesBot().run();
	}
	
	@Override
	public String getPrefix() {
		return ",";
	}

	@Override
	public void onLoad() {
		DB.getInstance().load();
		CommonErrorManager.reload();
		CommandCommonError.reloadAfterCommonError();
	}
	
	@Override
	public void onReload() {
		DB.getInstance().reload();
		CommonErrorManager.reload();
		CommandCommonError.reloadAfterCommonError();
	}
	
	@Override
	public void registerEventListeners(List<EventBase> events) {
		events.add(new CrashReportEventHandler(this));
		events.add(new ReactionRolesListener(this));
		events.add(new PlayerCounter(this));
		events.add(new AutoRemoveBirthdayRole(this));
		events.add(new OCRCommonErrors(this));
		events.add(new ImageOnlyChannel(this));
		events.add(new LikeDislikePollEvents(this));
	}

	@Override
	public void registerEveryoneCommand(List<EveryoneCommand> cmds) {
		cmds.add(new CommandCommonError(this));
	}
	
	@Override
	public void registerChatModCommand(List<ChatModCommand> cmds) {
		cmds.add(new CommandLookup(this));
	}

	@Override
	public void registerOwnerCommand(List<OwnerCommand> cmds) {
		cmds.add(new CommandAddReaction(this));
		cmds.add(new CommandYoutube(this));
	}

}
