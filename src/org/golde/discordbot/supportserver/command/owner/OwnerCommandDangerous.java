package org.golde.discordbot.supportserver.command.owner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class OwnerCommandDangerous extends OwnerCommand {

	public OwnerCommandDangerous(@Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {

		super(nameIn, argsIn, helpIn, aliasesIn);

		this.ownerCommand = true; //Makes it so only from my user account, I can run the command. Disreguards any roles what so ever.
	}

}
