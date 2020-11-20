package org.golde.discordbot.shared.db;

public interface ICanHasDatabaseFile {
	public default void loadOnce() {reload();};
	public void reload();
}
