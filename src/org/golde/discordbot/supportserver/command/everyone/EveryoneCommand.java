package org.golde.discordbot.supportserver.command.everyone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.golde.discordbot.supportserver.command.BaseCommand;

public abstract class EveryoneCommand extends BaseCommand {

	private static final Category CATEGORY_EVERYONE = new Category("Everyone");
	
	public EveryoneCommand(@Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		super(nameIn, argsIn, helpIn, aliasesIn);
		this.category = CATEGORY_EVERYONE;
	}

}
