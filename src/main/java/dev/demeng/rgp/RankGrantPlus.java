package dev.demeng.rgp;

import dev.demeng.demlib.Common;
import dev.demeng.demlib.JoinNotification;
import dev.demeng.demlib.Registerer;
import dev.demeng.demlib.command.CommandMessages;
import dev.demeng.demlib.connection.SpigotUpdateChecker;
import dev.demeng.demlib.core.DemLib;
import dev.demeng.demlib.file.LogFile;
import dev.demeng.demlib.file.YamlFile;
import dev.demeng.demlib.message.MessageUtils;
import dev.demeng.rgp.command.GrantCmd;
import dev.demeng.rgp.command.RankGrantPlusCmd;
import dev.demeng.rgp.task.DurationTask;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

public final class RankGrantPlus extends JavaPlugin {

  @Getter public YamlFile settingsFile;
  @Getter public YamlFile messagesFile;
  @Getter public YamlFile ranksFile;
  @Getter public YamlFile dataFile;

  @Getter private LogFile grantLogs;

  private static final int SETTINGS_VERSION = 5;
  private static final int MESSAGES_VERSION = 6;
  private static final int RANKS_VERSION = 2;

  @Getter private Permission permission = null;

  @Override
  public void onEnable() {

    final long startTime = System.currentTimeMillis();

    DemLib.setPlugin(this);
    DemLib.setPrefix("&8[&2RankGrant+&8] &r");

    getLogger().info("Loading files...");
    if (!setupFiles()) return;

    getLogger().info("Registering commands...");
    DemLib.setCommandMessages(new CommandMessages(getMessages()));

    try {
      Registerer.registerCommand(new RankGrantPlusCmd(this));
      Registerer.registerCommand(new GrantCmd(this));

    } catch (NoSuchFieldException | IllegalAccessException ex) {
      MessageUtils.error(ex, "Failed to register commands.", true);
      return;
    }

    getLogger().info("Registering listeners...");
    new JoinNotification(UUID.fromString("ca19af04-a156-482e-a35d-3f5f434975b5"));

    getLogger().info("Hooking into Vault...");
    if (!setupPermissions()) {
      MessageUtils.error(null, "Failed to hook into Vault.", true);
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

  private boolean setupPermissions() {
    RegisteredServiceProvider<Permission> rsp =
        getServer().getServicesManager().getRegistration(Permission.class);
    permission = rsp.getProvider();
    return permission != null;
  }

  private boolean setupFiles() {

    try {
      settingsFile = new YamlFile("settings.yml");
      messagesFile = new YamlFile("messages.yml");
      ranksFile = new YamlFile("ranks.yml");
      dataFile = new YamlFile("data.yml");
    } catch (final Exception ex) {
      MessageUtils.error(ex, "Failed to load configuration files.", true);
      return false;
    }

    if (!settingsFile.configUpToDate(SETTINGS_VERSION)) {
      MessageUtils.error(null, "Outdated settings.yml file.", true);
      return false;
    }
    if (!messagesFile.configUpToDate(MESSAGES_VERSION)) {
      MessageUtils.error(null, "Outdated messages.yml file.", true);
      return false;
    }
    if (!ranksFile.configUpToDate(RANKS_VERSION)) {
      MessageUtils.error(null, "Outdated ranks.yml file.", true);
      return false;
    }

    MessageUtils.setPrefix(getMessages().getString("prefix"));

    try {
      grantLogs = new LogFile();
    } catch (final Exception ex) {
      MessageUtils.error(ex, "Failed to load log file.", true);
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
        MessageUtils.error(ex, "Failed to save data.", true);
        return null;
      }

      if (permsPlugin == null) {
        MessageUtils.error(null, "Grant/ungrant commands are not set (settings.yml)", true);
      }

      return permsPlugin;
    }
    return null;
  }
}
