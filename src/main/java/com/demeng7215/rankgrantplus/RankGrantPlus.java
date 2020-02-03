package com.demeng7215.rankgrantplus;

import com.demeng7215.demlib.DemLib;
import com.demeng7215.demlib.api.Common;
import com.demeng7215.demlib.api.DeveloperNotifications;
import com.demeng7215.demlib.api.Registerer;
import com.demeng7215.demlib.api.SpigotUpdateChecker;
import com.demeng7215.demlib.api.files.CustomConfig;
import com.demeng7215.demlib.api.files.CustomLog;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.commands.GrantCmd;
import com.demeng7215.rankgrantplus.commands.RankGrantPlusCmd;
import com.demeng7215.rankgrantplus.utils.TempGrantTask;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collections;

public final class RankGrantPlus extends JavaPlugin {

    /* ERROR CODES
    1: Failed to load configuration files.
    2: Outdated configuration file.
    3: Failed to load log file.
    4: Failed to hook into Vault.
    5: Failed to save data.
    6: Commands not set up.
     */

	@Getter
	public CustomConfig configFile, languageFile, ranksFile, dataFile;

	private CustomLog grantLogs;

	private static final int CONFIGURATION_VERSION = 4;
	private static final int LANGUAGE_VERSION = 4;
	private static final int RANKS_VERSION = 1;

	private Permission perms = null;

	@Override
	public void onEnable() {

		final long startTime = System.currentTimeMillis();

		DemLib.setPlugin(this, "N/A");

		MessageUtils.setPrefix("&8[&2RankGrant+&8] &r");

		MessageUtils.console("Beginning to enable RankGrant+...\n\n" +
				"&a__________                __     ________                     __               \n" +
				"&a\\______   \\_____    ____ |  | __/  _____/___________    _____/  |_     .__     \n" +
				"&a |       _/\\__  \\  /    \\|  |/ /   \\  __\\_  __ \\__  \\  /    \\   __\\  __|  |___ \n" +
				"&2 |    |   \\ / __ \\|   |  \\    <\\    \\_\\  \\  | \\// __ \\|   |  \\  |   /__    __/ \n" +
				"&2 |____|_  /(____  /___|  /__|_ \\\\______  /__|  (____  /___|  /__|      |__|    \n" +
				"&2        \\/      \\/     \\/     \\/       \\/           \\/     \\/                  \n\n");

		getLogger().info("Loading files...");
		if (!setupFiles()) return;

		getLogger().info("Registering commands...");
		Registerer.registerCommand(new RankGrantPlusCmd(this));
		Registerer.registerCommand(new GrantCmd(this));

		getLogger().info("Registering listeners...");
		DeveloperNotifications.enableNotifications("ca19af04-a156-482e-a35d-3f5f434975b5");

		getLogger().info("Hooking into Vault...");
		if (!setupPermissions()) {
			MessageUtils.error(null, 4, "Failed to hook into Vault.", true);
			return;
		}

		final String permsPlugin = setupCommands();

		if (permsPlugin != null) getLogger().info("Automatically setup " +
				"RankGrant+ to work with " + permsPlugin + ".");

		if (!isEnabled()) return;

		getLogger().info("Starting tasks...");
		TempGrantTask task = new TempGrantTask(this);
		task.runTaskTimer(this, 0L, 20L);

		getLogger().info("Loading metrics...");
		new Metrics(this);

		SpigotUpdateChecker.checkForUpdates(63403);

		final long loadTime = System.currentTimeMillis() - startTime;

		MessageUtils.console("&aRankGrant+ v" + Common.getVersion() +
				" by Demeng7215 has been successfully enabled in " + loadTime + "ms.");
	}

	@Override
	public void onDisable() {
		MessageUtils.console("&cRankGrant+ v" + Common.getVersion() +
				" by Demeng7215 has been successfully disabled.");
	}

	public FileConfiguration getConfiguration() {
		return this.configFile.getConfig();
	}

	public FileConfiguration getLang() {
		return this.languageFile.getConfig();
	}

	public FileConfiguration getRanks() {
		return this.ranksFile.getConfig();
	}

	public FileConfiguration getData() {
		return this.dataFile.getConfig();
	}

	public CustomLog getGrantLogs() {
		return this.grantLogs;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public Permission getPermissions() {
		return perms;
	}

	private boolean setupFiles() {

		try {
			configFile = new CustomConfig("configuration.yml");
			languageFile = new CustomConfig("language.yml");
			ranksFile = new CustomConfig("ranks.yml");
			dataFile = new CustomConfig("data.yml");
		} catch (final Exception ex) {
			MessageUtils.error(ex, 1, "Failed to load configuration files.", true);
			return false;
		}

		if (getConfiguration().getInt("config-version") != CONFIGURATION_VERSION) {
			MessageUtils.error(new Exception(), 2, "Outdated configuration.yml file.", true);
			return false;
		}

		if (getLang().getInt("config-version") != LANGUAGE_VERSION) {
			MessageUtils.error(new Exception(), 2, "Outdated language.yml file.", true);
			return false;
		}

		if (getRanks().getInt("config-version") != RANKS_VERSION) {
			MessageUtils.error(new Exception(), 2, "Outdated ranks.yml file.", true);
			return false;
		}

		MessageUtils.setPrefix(getLang().getString("prefix"));

		try {
			grantLogs = new CustomLog("logs.txt");
		} catch (final Exception ex) {
			MessageUtils.error(ex, 3, "Failed to load log file.", true);
			return false;
		}

		return true;
	}

	public static String stripColorCodes(String s) {
		return ChatColor.stripColor(MessageUtils.colorize(s));
	}

	private String setupCommands() {

		if (getConfiguration().getStringList("commands.grant").contains("none")
				|| getConfiguration().getStringList("commands.ungrant").contains("none")) {

			String permsPlugin = null;

			if (Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
				getConfiguration().set("commands.grant", Collections.singletonList("pex user %target% group set %rank%"));
				getConfiguration().set("commands.ungrant", Collections.singletonList("pex user %target% group set default"));
				permsPlugin = "PermissionsEx";
			}

			if (Bukkit.getServer().getPluginManager().getPlugin("UltraPermissions") != null) {
				getConfiguration().set("commands.grant", Collections.singletonList("upc setGroups %target% %rank%"));
				getConfiguration().set("commands.ungrant", Collections.singletonList("upc setGroups %target% %rank%"));
				permsPlugin = "UltraPermissions";
			}

			if (Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
				getConfiguration().set("commands.grant", Collections.singletonList("lp user %target% parent set %rank%"));
				getConfiguration().set("commands.ungrant", Collections.singletonList("lp user %target% parent set default"));
				permsPlugin = "LuckPerms";
			}

			if (Bukkit.getServer().getPluginManager().getPlugin("GroupManager") != null) {
				getConfiguration().set("commands.grant", Collections.singletonList("manuadd %target% %rank%"));
				getConfiguration().set("commands.ungrant", Collections.singletonList("manudel %target%"));
				permsPlugin = "GroupManager";
			}

			try {
				this.configFile.saveConfig();
			} catch (final IOException ex) {
				MessageUtils.error(ex, 5, "Failed to save data.", true);
				return null;
			}

			if (permsPlugin == null) {
				MessageUtils.error(null, 6,
						"Grant/ungrant commands are not set (configuration.yml)", true);
			}

			return permsPlugin;
		}
		return null;
	}
}
