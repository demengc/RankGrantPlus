/*
 * MIT License
 *
 * Copyright (c) 2021 Demeng Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.demeng.rankgrantplus;

import dev.demeng.pluginbase.BaseSettings;
import dev.demeng.pluginbase.Common;
import dev.demeng.pluginbase.TaskUtils;
import dev.demeng.pluginbase.UpdateChecker;
import dev.demeng.pluginbase.UpdateChecker.Result;
import dev.demeng.pluginbase.YamlConfig;
import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.pluginbase.plugin.BaseManager;
import dev.demeng.pluginbase.plugin.BasePlugin;
import dev.demeng.rankgrantplus.commands.RankGrantPlusCmd;
import dev.demeng.rankgrantplus.util.SupportedPermissionPlugin;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * The main class for RankGrant+.
 */
public final class RankGrantPlus extends BasePlugin {

  // Managers for the corresponding configuration file.
  @Getter private YamlConfig settingsFile;
  @Getter private YamlConfig messagesFile;
  @Getter private YamlConfig ranksFile;
  @Getter private YamlConfig dataFile;

  // Versions of the corresponding configuration file.
  private static final int SETTINGS_VERSION = 6;
  private static final int MESSAGES_VERSION = 7;
  private static final int RANKS_VERSION = 3;
  private static final int DATA_VERSION = 2;

  // Vault API permission hook.
  @Getter private Permission permissionHook;

  @Override
  public void enable() {

    final long startTime = System.currentTimeMillis();

    ChatUtils.coloredConsole("\n\n"
        + "&2__________  ________             \n"
        + "&2\\______   \\/  _____/     .__     \n"
        + "&2 |       _/   \\  ___   __|  |___ \n"
        + "&a |    |   \\    \\_\\  \\ /__    __/ \n"
        + "&a |____|_  /\\______  /    |__|    \n"
        + "&a        \\/        \\/             \n\n");

    getLogger().info("Loading configuration files...");
    if (!loadFiles()) {
      return;
    }

    getLogger().info("Initializing base settings...");
    BaseManager.setBaseSettings(new RankGrantPlus.Settings());

    getLogger().info("Hooking into Vault and permission plugin...");
    if (!hookPermission() || !initialSetup()) {
      return;
    }

    getLogger().info("Registering commands...");
    getCommandManager().register(new RankGrantPlusCmd(this));

    getLogger().info("Loading metrics...");
    loadMetrics();

    getLogger().info("Checking for updates...");
    checkUpdates();

    ChatUtils.console("&aRankGrant+ v" + Common.getVersion()
        + " by Demeng has been enabled in "
        + (System.currentTimeMillis() - startTime) + " ms.");

    ChatUtils.coloredConsole("&6Enjoying RG+? Check out GrantX! &ehttps://demeng.dev/grantx");
  }

  @Override
  public void disable() {
    ChatUtils.console("&cRankGrant+ v" + Common.getVersion() + " by Demeng has been disabled.");
  }

  /**
   * Loads all configuration files and performs a quick version check to make sure the file is not
   * outdated.
   *
   * @return true if successful, false otherwise
   */
  private boolean loadFiles() {

    // Name of the file that is currently being loading, used for the error message.
    String currentlyLoading = "configuration files";

    try {
      currentlyLoading = "settings.yml";
      settingsFile = new YamlConfig(currentlyLoading);

      if (settingsFile.isOutdated(SETTINGS_VERSION)) {
        Common.error(null, "Outdated settings.yml file.", true);
        return false;
      }

      currentlyLoading = "messages.yml";
      messagesFile = new YamlConfig(currentlyLoading);

      if (messagesFile.isOutdated(MESSAGES_VERSION)) {
        Common.error(null, "Outdated messages.yml file.", true);
        return false;
      }

      currentlyLoading = "ranks.yml";
      ranksFile = new YamlConfig(currentlyLoading);

      if (ranksFile.isOutdated(RANKS_VERSION)) {
        Common.error(null, "Outdated ranks.yml file.", true);
        return false;
      }

      currentlyLoading = "data.yml";
      dataFile = new YamlConfig(currentlyLoading);

      if (dataFile.isOutdated(DATA_VERSION)) {
        Common.error(null, "Outdated data.yml file.", true);
        return false;
      }

    } catch (IOException | InvalidConfigurationException ex) {
      Common.error(ex, "Failed to load " + currentlyLoading + ".", true);
      return false;
    }

    return true;
  }

  /**
   * Hooks into a permission plugin using Vault.
   *
   * @return true if successful, false otherwise
   */
  private boolean hookPermission() {

    final RegisteredServiceProvider<Permission> provider =
        Bukkit.getServer().getServicesManager().getRegistration(Permission.class);

    if (provider == null) {
      Common.error(null, "Failed to hook into Vault and/or a permission plugin.", true);
      return false;
    }

    permissionHook = provider.getProvider();
    return true;
  }

  /**
   * Performs the initial setup of the activation and expiration commands if they have not already
   * been set and a supported permission plugin is being used. A list of plugins that can be
   * automatically set up can be found in {@link SupportedPermissionPlugin}.
   *
   * @return true if OK to continue, false otherwise
   */
  private boolean initialSetup() {

    // Commands for grant activation.
    final List<String> activation = new ArrayList<>(
        getSettings().getStringList("commands.activation"));

    // Commands for grant expiration.
    final List<String> expiration = new ArrayList<>(
        getSettings().getStringList("commands.expiration"));

    if (!activation.isEmpty() && !expiration.isEmpty()) {
      return true;
    }

    for (SupportedPermissionPlugin plugin : SupportedPermissionPlugin.values()) {
      if (Bukkit.getPluginManager().getPlugin(plugin.getName()) != null) {

        activation.addAll(plugin.getActivationCommands());
        expiration.addAll(plugin.getExpirationCommands());

        getSettings().set("commands.activation", activation);
        getSettings().set("commands.expiration", expiration);

        try {
          settingsFile.save();
        } catch (IOException ex) {
          Common.error(ex, "Failed to save settings.yml.", true);
          return false;
        }

        getLogger().warning("Successfully completed initial setup for " + plugin.getName() + ".");
        return true;
      }
    }

    Common.error(null, "Activation/expiration commands are not set in settings.yml.", true);
    return false;
  }

  /**
   * Loads bStats metrics (sends stats if enabled in bStats config).
   */
  private void loadMetrics() {
    try {
      new Metrics(this, 3766);
    } catch (IllegalStateException ex) {
      if (ex.getMessage().equals("bStats Metrics class has not been relocated correctly!")) {
        // Send warning instead of disabling, since bStats is not relocated when I'm testing.
        getLogger().warning("bStats has not been relocated, skipping.");
      }
    }
  }

  /**
   * Checks if the current plugin version matches the one on SpigotMC.
   */
  private void checkUpdates() {
    TaskUtils.runAsync(task -> {
      final UpdateChecker checker = new UpdateChecker(63403);

      if (checker.getResult() == Result.OUTDATED) {
        ChatUtils.coloredConsole(
            "&2" + ChatUtils.CONSOLE_LINE,
            "&aA newer version of RankGrant+ is available!",
            "&aCurrent version: &r" + Common.getVersion(),
            "&aLatest version: &r" + checker.getLatestVersion(),
            "&aGet the update: &rhttps://spigotmc.org/resources/63403",
            "&2" + ChatUtils.CONSOLE_LINE);
        return;
      }

      if (checker.getResult() == Result.ERROR) {
        getLogger().warning("Failed to check for updates.");
      }
    });
  }

  public FileConfiguration getSettings() {
    return settingsFile.getConfig();
  }

  public FileConfiguration getMessages() {
    return messagesFile.getConfig();
  }

  public FileConfiguration getRanks() {
    return ranksFile.getConfig();
  }

  public FileConfiguration getData() {
    return dataFile.getConfig();
  }

  /**
   * The settings to use for PluginBase.
   */
  private class Settings implements BaseSettings {

    @Override
    public String prefix() {
      return getMessages().getString("prefix");
    }

    @Override
    public String notPlayer() {
      return getMessages().getString("not-player");
    }

    @Override
    public String insufficientPermission() {
      return getMessages().getString("insufficient-permission");
    }

    @Override
    public String incorrectUsage() {
      return getMessages().getString("incorrect-usage");
    }
  }
}
