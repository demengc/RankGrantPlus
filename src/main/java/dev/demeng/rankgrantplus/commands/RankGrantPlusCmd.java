package dev.demeng.rankgrantplus.commands;

import dev.demeng.demlib.api.Common;
import dev.demeng.demlib.api.commands.CommandSettings;
import dev.demeng.demlib.api.commands.types.BaseCommand;
import dev.demeng.demlib.api.messages.MessageUtils;
import dev.demeng.rankgrantplus.RankGrantPlus;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class RankGrantPlusCmd implements BaseCommand {

  private final RankGrantPlus i;

  public RankGrantPlusCmd(RankGrantPlus i) {
    this.i = i;
  }

  @Override
  public CommandSettings getSettings() {
    return i.getCommandSettings();
  }

  @Override
  public String getName() {
    return "rankgrantplus";
  }

  @Override
  public List<String> getAliases() {
    return Collections.singletonList("rankgrant+");
  }

  @Override
  public boolean isPlayerCommand() {
    return false;
  }

  @Override
  public String getPermission() {
    return null;
  }

  @Override
  public String getUsage() {
    return "";
  }

  @Override
  public int getArgs() {
    return 0;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    MessageUtils.tellWithoutPrefix(
        sender,
        "&a&lRunning RankGrant+ v" + Common.getVersion() + " by Demeng.",
        "&aLink: &fhttps://spigotmc.org/resources/63403/",
        "&aLike RG+? Check out GrantX: &fdemeng.dev/grantx");
  }
}
