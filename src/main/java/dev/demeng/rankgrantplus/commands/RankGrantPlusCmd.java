package dev.demeng.rankgrantplus.commands;

import dev.demeng.pluginbase.Common;
import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.pluginbase.command.CommandBase;
import dev.demeng.pluginbase.command.annotations.Aliases;
import dev.demeng.pluginbase.command.annotations.Command;
import dev.demeng.pluginbase.command.annotations.Default;
import dev.demeng.pluginbase.command.annotations.Description;
import dev.demeng.pluginbase.command.annotations.Permission;
import dev.demeng.pluginbase.command.annotations.SubCommand;
import dev.demeng.rankgrantplus.RankGrantPlus;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * The main command of RankGrant+.
 */
@RequiredArgsConstructor
@Command("rankgrantplus")
@Aliases({"rgp"})
public class RankGrantPlusCmd extends CommandBase {

  private final RankGrantPlus i;

  @Default
  @Description("Displays information for RankGrant+.")
  public void runDefault(CommandSender sender) {
    ChatUtils.coloredTell(
        sender,
        "&a&lRunning RankGrant+ v" + Common.getVersion() + " by Demeng.",
        "&aLink: &fhttps://spigotmc.org/resources/63403/",
        "&aEnjoying RG+? Check out GrantX! &fdemeng.dev/grantx");
  }

  @SubCommand("reload")
  @Description("Reloads configuration files.")
  @Aliases("rl")
  @Permission("rankgrantplus.reload")
  public void runReload(CommandSender sender) {

    try {
      i.getSettingsFile().reload();
      i.getMessagesFile().reload();
      i.getRanksFile().reload();
      i.getDataFile().reload();
    } catch (IOException | InvalidConfigurationException ex) {
      Common.error(ex, "Failed to reload config files.", false, sender);
      return;
    }

    ChatUtils.tell(sender, i.getMessages().getString("reloaded"));
  }
}
