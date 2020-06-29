package dev.demeng.rankgrantplus;

import dev.demeng.demlib.DemLib;
import dev.demeng.demlib.api.Common;
import dev.demeng.demlib.api.DeveloperNotifications;
import dev.demeng.demlib.api.Registerer;
import dev.demeng.demlib.api.commands.CommandSettings;
import dev.demeng.demlib.api.connections.SpigotUpdateChecker;
import dev.demeng.demlib.api.files.CustomConfig;
import dev.demeng.demlib.api.files.CustomLog;
import dev.demeng.demlib.api.messages.MessageUtils;
import dev.demeng.rankgrantplus.commands.GrantCmd;
import dev.demeng.rankgrantplus.commands.RankGrantPlusCmd;
import dev.demeng.rankgrantplus.commands.ReloadCmd;
import dev.demeng.rankgrantplus.utils.DurationTask;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
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

  @Getter public CustomConfig settingsFile;
  @Getter public CustomConfig messagesFile;
  @Getter public CustomConfig ranksFile;
  @Getter public CustomConfig dataFile;

  @Getter private CustomLog grantLogs;

  @Getter private CommandSettings commandSettings;

  private static final int SETTINGS_VERSION = 5;
  private static final int MESSAGES_VERSION = 5;
  private static final int RANKS_VERSION = 2;

  @Getter private Permission permission = null;

  @Override
  public void onEnable() {

    final long startTime = System.currentTimeMillis();

    DemLib.setPlugin(this);
    MessageUtils.setPrefix("&8[&2RankGrant+&8] &r");

    getLogger().info("Loading files...");
    if (!setupFiles()) return;

    getLogger().info("Registering commands...");
    this.commandSettings = new CommandSettings();
    commandSettings.setIncorrectUsageMessage(getMessages().getString("invalid-args"));
    commandSettings.setNotPlayerMessage(getMessages().getString("console"));
    commandSettings.setNoPermissionMessage(getMessages().getString("no-perms"));

    Registerer.registerCommand(new RankGrantPlusCmd(this));
    Registerer.registerCommand(new ReloadCmd(this));
    Registerer.registerCommand(new GrantCmd(this));

    getLogger().info("Registering listeners...");
    DeveloperNotifications.enableNotifications("ca19af04-a156-482e-a35d-3f5f434975b5");

    getLogger().info("Hooking into Vault...");
    if (!setupPermsHook()) {
      MessageUtils.error(null, 4, "Failed to hook into Vault.", true);
      return;
    }

    final String permsPlugin = setupCommands();

    if (permsPlugin != null)
      getLogger().info("Automatically setup " + "RankGrant+ to work with " + permsPlugin + ".");

    if (!isEnabled()) return;

    getLogger().info("Launching tasks...");
    new DurationTask(this).runTaskTimer(this, 20L, 20L);

    getLogger().info("Loading metrics...");
    new Metrics(this, 3766);

    SpigotUpdateChecker.checkForUpdates(63403);

    final long loadTime = System.currentTimeMillis() - startTime;

    MessageUtils.console(
        "&aRankGrant+ v"
            + Common.getVersion()
            + " by Demeng has been successfully enabled in "
            + loadTime
            + "ms.");

    MessageUtils.console("&6Like RG+? Check out GrantX: &ehttps://demeng.dev/grantx");
  }

  @Override
  public void onDisable() {
    MessageUtils.console(
        "&cRankGrant+ v" + Common.getVersion() + " by Demeng has been successfully disabled.");
  }

  public FileConfiguration getSettings() {
    return this.settingsFile.getConfig();
  }

  public FileConfiguration getMessages() {
    return this.messagesFile.getConfig();
  }

  public FileConfiguration getRanks() {
    return this.ranksFile.getConfig();
  }

  public FileConfiguration getData() {
    return this.dataFile.getConfig();
  }

  private boolean setupPermsHook() {
    final RegisteredServiceProvider<Permission> rsp =
        getServer().getServicesManager().getRegistration(Permission.class);
    this.permission = rsp.getProvider();
    return this.permission != null;
  }

  private boolean setupFiles() {

    try {
      settingsFile = new CustomConfig("settings.yml");
      messagesFile = new CustomConfig("messages.yml");
      ranksFile = new CustomConfig("ranks.yml");
      dataFile = new CustomConfig("data.yml");
    } catch (final Exception ex) {
      MessageUtils.error(ex, 1, "Failed to load configuration files.", true);
      return false;
    }

    if (!settingsFile.configUpToDate(SETTINGS_VERSION)) {
      MessageUtils.error(null, 2, "Outdated settings.yml file.", true);
      return false;
    }
    if (!messagesFile.configUpToDate(MESSAGES_VERSION)) {
      MessageUtils.error(null, 2, "Outdated messages.yml file.", true);
      return false;
    }
    if (!ranksFile.configUpToDate(RANKS_VERSION)) {
      MessageUtils.error(null, 2, "Outdated ranks.yml file.", true);
      return false;
    }

    MessageUtils.setPrefix(getMessages().getString("prefix"));

    try {
      grantLogs = new CustomLog();
    } catch (final Exception ex) {
      MessageUtils.error(ex, 3, "Failed to load log file.", true);
      return false;
    }

    return true;
  }

  private String setupCommands() {

    if (getSettings().getStringList("commands.grant").contains("none")
        || getSettings().getStringList("commands.ungrant").contains("none")) {

      String permsPlugin = null;

      if (Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
        getSettings()
            .set("commands.grant", Collections.singletonList("pex user %target% group set %rank%"));
        getSettings()
            .set(
                "commands.ungrant",
                Collections.singletonList("pex user %target% group set default"));
        permsPlugin = "PermissionsEx";
      }

      if (Bukkit.getServer().getPluginManager().getPlugin("UltraPermissions") != null) {
        getSettings()
            .set("commands.grant", Collections.singletonList("upc setGroups %target% %rank%"));
        getSettings()
            .set("commands.ungrant", Collections.singletonList("upc setGroups %target% %rank%"));
        permsPlugin = "UltraPermissions";
      }

      if (Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
        getSettings()
            .set("commands.grant", Collections.singletonList("lp user %target% parent set %rank%"));
        getSettings()
            .set(
                "commands.ungrant",
                Collections.singletonList("lp user %target% parent set default"));
        permsPlugin = "LuckPerms";
      }

      if (Bukkit.getServer().getPluginManager().getPlugin("GroupManager") != null) {
        getSettings().set("commands.grant", Collections.singletonList("manuadd %target% %rank%"));
        getSettings().set("commands.ungrant", Collections.singletonList("manudel %target%"));
        permsPlugin = "GroupManager";
      }

      try {
        this.settingsFile.saveConfig();
      } catch (final IOException ex) {
        MessageUtils.error(ex, 5, "Failed to save data.", true);
        return null;
      }

      if (permsPlugin == null) {
        MessageUtils.error(null, 6, "Grant/ungrant commands are not set (settings.yml)", true);
      }

      return permsPlugin;
    }
    return null;
  }
}
