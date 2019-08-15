package com.demeng7215.rankgrantplus.commands;

import com.demeng7215.demapi.api.DemCommand;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import com.demeng7215.rankgrantplus.inventories.RankSelectInv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GrantCmd extends DemCommand {

    private final RankGrantPlus i;

    public GrantCmd(RankGrantPlus i) {
        super("grant");

        setDescription("Grant a player a rank.");
        setAliases(Arrays.asList("appoint", "grantrank", "appointrank"));

        this.i = i;
    }

    @Override
    protected void run(CommandSender sender, String[] args) {

        if(!i.isEnabled()) return;

        if (!checkIsPlayer(sender, i.getLanguage().getString("console"))) return;

        if (!checkArgsStrict(args, 1, sender, i.getLanguage().getString("invalid-args"))) return;

        if(!checkHasPermission("rankgrantplus.grant", sender,
                i.getLanguage().getString("no-perms"))) return;

        final Player p = (Player) sender;

        new RankSelectInv(i, Bukkit.getOfflinePlayer(args[0]), p).open((Player) sender);
    }
}
