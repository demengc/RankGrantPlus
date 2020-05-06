package com.demeng7215.rankgrantplus.commands;

import com.demeng7215.demlib.api.CustomCommand;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import com.demeng7215.rankgrantplus.inventories.RankSelectInv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCmd extends CustomCommand {

  private RankGrantPlus i;

  public GrantCmd(RankGrantPlus i) {
    super("grant");

    this.i = i;

    setDescription("Grant a player a rank.");
  }

  @Override
  protected void run(CommandSender sender, String[] args) {

    if (!i.isEnabled()) {
      MessageUtils.tellWithoutPrefix(
          sender,
          "&cRankGrant+ has already been disabled due to an error "
              + "prior to this. Please resolve that error and try granting again.");
      return;
    }

    if (!checkIsPlayer(sender, i.getMessages().getString("console"))) return;

    if (!checkArgsStrict(args, 1, sender, i.getMessages().getString("invalid-args"))) return;

    if (!checkHasPerm("rankgrantplus.grant", sender, i.getMessages().getString("no-perms"))) return;

    final Player p = (Player) sender;

    new RankSelectInv(i, Bukkit.getOfflinePlayer(args[0]), p);
  }
}
