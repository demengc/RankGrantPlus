package dev.demeng.rankgrantplus.commands;

import dev.demeng.demlib.api.commands.CommandSettings;
import dev.demeng.demlib.api.commands.types.SubCommand;
import dev.demeng.demlib.api.messages.MessageUtils;
import dev.demeng.rankgrantplus.RankGrantPlus;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCmd implements SubCommand {

  private final RankGrantPlus i;

  public ReloadCmd(RankGrantPlus i) {
    this.i = i;
  }

  @Override
  public String getBaseCommand() {
    return "rankgrantplus";
  }

  @Override
  public CommandSettings getSettings() {
    return i.getCommandSettings();
  }

  @Override
  public String getName() {
    return "reload";
  }

  @Override
  public List<String> getAliases() {
    return Collections.emptyList();
  }

  @Override
  public boolean isPlayerCommand() {
    return false;
  }

  @Override
  public String getPermission() {
    return "rankgrantplus.reload";
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
    i.settingsFile.reloadConfig();
    i.messagesFile.reloadConfig();
    i.ranksFile.reloadConfig();

    MessageUtils.tell(sender, i.getMessages().getString("reloaded"));
  }
}
