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
import org.mineacademy.remain.Remain;

public class RankGrantPlus extends JavaPlugin {

    /* ERROR CODES
    1: Failed to load configuration files.
    2: Outdated configuration file.
    3: Failed to load log file.
    4: Failed to hook into Vault.
    5: Failed to save data.
    6: Commands not set up.
     */

    public DemConfigurationFile config;
    public DemConfigurationFile language;
    public DemConfigurationFile ranks;
    public DemConfigurationFile data;
    private DemLogFile grantLogs;

    private static final int CONFIGURATION_VERSION = 3;
    private static final int LANGUAGE_VERSION = 3;
    private static final int RANKS_VERSION = 1;

    private Permission perms = null;

    @Override
    public void onEnable() {

        DemAPI.setPlugin(this);
        MessageUtils.setPrefix("[RankGrant+] ");
        Remain.setPlugin(this);

        MessageUtils.sendColoredConsoleMessage("Enabling RankGrant+...\n" +
        "&a ########     ###    ##    ## ##    ##  ######   ########     ###    ##    ## ########        \n" +
        "&a ##     ##   ## ##   ###   ## ##   ##  ##    ##  ##     ##   ## ##   ###   ##    ##      ##   \n" +
        "&a ##     ##  ##   ##  ####  ## ##  ##   ##        ##     ##  ##   ##  ####  ##    ##      ##   \n" +
        "&a ########  ##     ## ## ## ## #####    ##   #### ########  ##     ## ## ## ##    ##    ###### \n" +
        "&a ##   ##   ######### ##  #### ##  ##   ##    ##  ##   ##   ######### ##  ####    ##      ##   \n" +
        "&a ##    ##  ##     ## ##   ### ##   ##  ##    ##  ##    ##  ##     ## ##   ###    ##      ##   \n" +
        "&a ##     ## ##     ## ##    ## ##    ##  ######   ##     ## ##     ## ##    ##    ##           \n");

        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            this.getPluginLoader().disablePlugin(this);
            getLogger().warning("Reload detected. RankGrant+ is not compatible with reloads. Disabling...");
            return;
        }

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
        task.runTaskTimer(this,0L, 20L);
        getLogger().info("Started temporary-grant expiration timer.");

        getLogger().info("Loading metrics...");
        new Metrics(this);
        getLogger().info("Loaded metrics.");

        MessageUtils.sendSuccessfulEnableMessage();

        UpdateChecker.checkForUpdates(63403);

        if (this.getConfiguration().getStringList("commands.grant").contains("none")
                || this.getConfiguration().getStringList("commands.ungrant").contains("none")) {
            MessageUtils.error(new Exception("NullCommands"), 6, "Grant/ungrant command are not set up. " +
                    "(configuration.yml)", true);
            this.getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

        MessageUtils.sendSuccessfulDisableMessage();
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
