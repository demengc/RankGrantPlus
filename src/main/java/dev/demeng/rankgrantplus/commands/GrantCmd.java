package dev.demeng.rankgrantplus.commands;

import dev.demeng.demlib.api.commands.CommandSettings;
import dev.demeng.demlib.api.commands.types.BaseCommand;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.menus.RankSelectInv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GrantCmd implements BaseCommand {

  private final RankGrantPlus i;

  public GrantCmd(RankGrantPlus i) {
    this.i = i;
  }

  @Override
  public CommandSettings getSettings() {
    return i.getCommandSettings();
  }

  @Override
  public String getName() {
    return "grant";
  }

  @Override
  public List<String> getAliases() {
    return Collections.emptyList();
  }

  @Override
  public boolean isPlayerCommand() {
    return true;
  }

  @Override
  public String getPermission() {
    return "rankgrantplus.grant";
  }

  @Override
  public String getUsage() {
    return "<player>";
  }

  @Override
  public int getArgs() {
    return 1;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    final Player p = (Player) sender;
    new RankSelectInv(i, Bukkit.getOfflinePlayer(args[0]), p);
  }
}
