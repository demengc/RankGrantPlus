package dev.demeng.rgp.command;

import dev.demeng.demlib.command.CustomCommand;
import dev.demeng.rgp.RankGrantPlus;
import dev.demeng.rgp.menu.RankSelectInv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCmd extends CustomCommand {

  private final RankGrantPlus i;

  public GrantCmd(RankGrantPlus i) {
    super("grant", true, "rankgrantplus.grant", 1, "<player>");

    this.i = i;

    setDescription("Grant a rank.");
  }

  @Override
  protected void run(CommandSender sender, String[] args) {

    final Player p = (Player) sender;
    //noinspection deprecation
    new RankSelectInv(i, Bukkit.getOfflinePlayer(args[0]), p);
  }
}
