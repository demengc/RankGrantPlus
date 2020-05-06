package com.demeng7215.rankgrantplus.commands;

import com.demeng7215.demlib.api.Common;
import com.demeng7215.demlib.api.CustomCommand;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class RankGrantPlusCmd extends CustomCommand {

  private RankGrantPlus i;

  public RankGrantPlusCmd(RankGrantPlus i) {
    super("rankgrantplus");

    this.i = i;

    setDescription("Main command of RankGrant+.");
    setAliases(Collections.singletonList("rankgrant+"));
  }

  @Override
  protected void run(CommandSender sender, String[] args) {

    if (args.length == 0) {
      MessageUtils.tellWithoutPrefix(
          sender,
          "&a&lRunning RankGrant+ v" + Common.getVersion() + " by Demeng.",
          "&aLink: &fhttps://spigotmc.org/resources/63403/",
          "&aLike RG+? Check out GrantX: &fdemeng7215.com/grantx");
      return;
    }

    if (!checkArgsStrict(args, 1, sender, i.getMessages().getString("invalid-args"))) return;

    if (args[0].equalsIgnoreCase("reload")) {

      if (!checkHasPerm("rankgrantplus.reload", sender, i.getMessages().getString("no-perms")))
        return;

      i.settingsFile.reloadConfig();
      i.messagesFile.reloadConfig();
      i.ranksFile.reloadConfig();

      MessageUtils.tell(sender, i.getMessages().getString("reloaded"));
    }
  }
}
