package dev.demeng.rgp.command;

import dev.demeng.demlib.Common;
import dev.demeng.demlib.command.CustomCommand;
import dev.demeng.demlib.message.MessageUtils;
import dev.demeng.rgp.RankGrantPlus;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class RankGrantPlusCmd extends CustomCommand {

  private final RankGrantPlus i;

  public RankGrantPlusCmd(RankGrantPlus i) {
    super("rankgrantplus", false, null, 0, "");

    this.i = i;

    setDescription("Main command for RankGrant+.");
    setAliases(Arrays.asList("rankgrant+", "rg+"));
  }

  @Override
  protected void run(CommandSender sender, String[] args) {

    if (args.length == 1
        && args[0].equalsIgnoreCase("reload")) {

      if(!sender.hasPermission("rankgrantplus.reload")) {
        MessageUtils.tell(sender, i.getMessages().getString("no-permission"));
        return;
      }

      i.settingsFile.reloadConfig();
      i.messagesFile.reloadConfig();
      i.ranksFile.reloadConfig();

      MessageUtils.tell(sender, i.getMessages().getString("reloaded"));
      return;
    }

    MessageUtils.tellClean(
        sender,
        "&a&lRunning RankGrant+ v" + Common.getVersion() + " by Demeng.",
        "&aLink: &fhttps://spigotmc.org/resources/63403/",
        "&aLike RG+? Check out GrantX: &fdemeng.dev/grantx");
  }
}
