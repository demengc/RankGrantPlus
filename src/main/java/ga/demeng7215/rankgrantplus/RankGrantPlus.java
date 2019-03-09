package ga.demeng7215.rankgrantplus;

import ga.demeng7215.demapi.DemAPI;
import ga.demeng7215.demapi.api.*;
import ga.demeng7215.rankgrantplus.commands.GrantCmd;
import ga.demeng7215.rankgrantplus.commands.RankGrantPlusCmd;
import ga.demeng7215.rankgrantplus.utils.RGPInventoryListeners;
import ga.demeng7215.rankgrantplus.utils.TempGrantTask;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class RankGrantPlus extends JavaPlugin {

    /* ERROR CODES
    1: Failed to load configuration files.
    2: Outdated configuration file.
    3: Failed to load log file.
    4: Failed to hook into Vault.
    5: Failed to save data.
     */

    public DemConfigurationFile config;
    public DemConfigurationFile language;
    public DemConfigurationFile ranks;
    public DemConfigurationFile data;
    private DemLogFile grantLogs;

    private static final int CONFIGURATION_VERSION = 2;
    private static final int LANGUAGE_VERSION = 2;
    private static final int RANKS_VERSION = 1;

    private Permission perms = null;

    @Override
    public void onEnable() {

        DemAPI.setPlugin(this);
        MessageUtils.setPrefix("[RankGrant+] ");

        MessageUtils.sendColoredConsoleMessage("Enabling RankGrant+...\n" +
                "&a  _____             _     _____                 _   \n" +
                "&a |  __ \\           | |   / ____|               | |  \n" +
                "&a | |__) |__ _ _ __ | | _| |  __ _ __ __ _ _ __ | |_ \n" +
                "&a |  _  // _` | '_ \\| |/ / | |_ | '__/ _` | '_ \\| __|\n" +
                "&a | | \\ \\ (_| | | | |   <| |__| | | | (_| | | | | |_ \n" +
                "&a |_|  \\_\\__,_|_| |_|_|\\_\\\\_____|_|  \\__,_|_| |_|\\__|\n");

        getLogger().info("Loading files...");
        if (!setupFiles()) return;
        getLogger().info("Loaded files.");

        getLogger().info("Registering commands...");
        Registerer.registerCommand(new RankGrantPlusCmd(this));
        Registerer.registerCommand(new GrantCmd(this));
        getLogger().info("Registered commands.");

        getLogger().info("Registering listeners...");
        Registerer.registerListeners(new RGPInventoryListeners());
        DeveloperNotifications.enableNotifications("ca19af04-a156-482e-a35d-3f5f434975b5");
        getLogger().info("Registered listeners.");

        getLogger().info("Hooking into Vault...");
        if (!setupPermissions()) {
            MessageUtils.error(new Exception(), 4, "Failed to hook into Vault.", true);
            return;
        }
        getLogger().info("Hooked into Vault.");

        getLogger().info("Starting temporary-grant expiration timer...");
        TempGrantTask task = new TempGrantTask(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, task, 0L, 20L);
        getLogger().info("Started temporary-grant expiration timer.");

        getLogger().info("Loading metrics...");
        new Metrics(this);
        getLogger().info("Loaded metrics.");

        Common.sendSuccessfulEnableMessage();

        UpdateChecker.checkForUpdates(63403);
    }

    @Override
    public void onDisable() {

        Common.sendSuccessfulDisableMessage();
    }

    public FileConfiguration getConfiguration() {
        return this.config.getConfig();
    }

    public FileConfiguration getLanguage() {
        return this.language.getConfig();
    }

    public FileConfiguration getRanks() {
        return this.ranks.getConfig();
    }

    public FileConfiguration getData() {
        return this.data.getConfig();
    }

    public DemLogFile getGrantLogs() {
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
            config = new DemConfigurationFile("configuration.yml");
            language = new DemConfigurationFile("language.yml");
            ranks = new DemConfigurationFile("ranks.yml");
            data = new DemConfigurationFile("data.yml");
        } catch (final Exception ex) {
            MessageUtils.error(ex, 1, "Failed to load configuration files.", true);
            return false;
        }

        if (getConfiguration().getInt("config-version") != CONFIGURATION_VERSION) {
            MessageUtils.error(new Exception(), 2, "Outdated configuration file.", true);
            return false;
        }

        if (getLanguage().getInt("config-version") != LANGUAGE_VERSION) {
            MessageUtils.error(new Exception(), 2, "Outdated configuration file.", true);
            return false;
        }

        if (getRanks().getInt("config-version") != RANKS_VERSION) {
            MessageUtils.error(new Exception(), 2, "Outdated configuration file.", true);
            return false;
        }

        MessageUtils.setPrefix(getLanguage().getString("prefix"));

        if (getConfiguration().getBoolean("log-grants")) {
            try {
                grantLogs = new DemLogFile("logs.txt");
                grantLogs.log("RankGrant+ has been enabled.", true);
            } catch (final Exception ex) {
                MessageUtils.error(ex, 3, "Failed to load log file.", true);
                return false;
            }
        }

        return true;
    }

    public static String stripColorCodes(String s) {
        return ChatColor.stripColor(MessageUtils.color(s));
    }
}
