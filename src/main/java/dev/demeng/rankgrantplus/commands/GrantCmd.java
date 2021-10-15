package dev.demeng.rankgrantplus.commands;

import dev.demeng.pluginbase.command.CommandBase;
import dev.demeng.pluginbase.command.annotations.Command;
import dev.demeng.pluginbase.command.annotations.Completion;
import dev.demeng.pluginbase.command.annotations.Default;
import dev.demeng.pluginbase.command.annotations.Description;
import dev.demeng.pluginbase.command.annotations.Permission;
import dev.demeng.pluginbase.command.annotations.Usage;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.menus.RankSelectMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The command used for opening the grant GUI.
 */
@RequiredArgsConstructor
@Command("grant")
public class GrantCmd extends CommandBase {

  private final RankGrantPlus i;

  @Default
  @Description("Grants a player a rank.")
  @Permission("rankgrantplus.grant")
  @Usage("/grant <player>")
  public void runDefault(Player sender, @Completion("#players") String strTarget) {
    //noinspection deprecation
    new RankSelectMenu(i, sender, Bukkit.getOfflinePlayer(strTarget)).open(sender);
  }
}
