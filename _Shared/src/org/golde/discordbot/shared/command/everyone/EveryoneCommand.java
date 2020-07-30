package org.golde.discordbot.shared.command.everyone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.BaseCommand;

public abstract class EveryoneCommand extends BaseCommand {

	private static final Category CATEGORY_EVERYONE = new Category("Everyone");
	
	public EveryoneCommand(@Nonnull ESSBot bot, @Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		super(bot, nameIn, argsIn, helpIn, aliasesIn);
		this.category = CATEGORY_EVERYONE;
	}

}
