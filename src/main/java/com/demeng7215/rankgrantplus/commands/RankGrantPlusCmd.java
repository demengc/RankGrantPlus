package com.demeng7215.rankgrantplus.commands;

import com.demeng7215.demapi.api.Common;
import com.demeng7215.demapi.api.DemCommand;
import com.demeng7215.demapi.api.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class RankGrantPlusCmd extends DemCommand {

    private final RankGrantPlus i;

    public RankGrantPlusCmd(RankGrantPlus i) {
        super("rankgrantplus");

        setDescription("Main command of RankGrant+.");
        setAliases(Arrays.asList("rankgrant+", "rankgrant", "rankappointplus", "rankappoint+", "rankappoint"));

        this.i = i;
    }

    @Override
    protected void run(CommandSender sender, String[] args) {

        if (args.length == 0) {
            MessageUtils.sendMessageToCommandSender(sender, "&aRunning RankGrant+ v" +
                    Common.getVersion() + " by Demeng7215.");
            MessageUtils.sendMessageToCommandSender(sender, "&fhttps://spigotmc.org/resources/63403/");
            MessageUtils.sendMessageToCommandSender(sender, "&aType &f/grant <player> &ato grant a rank.");
            return;
        }

        if (!checkArgsStrict(args, 1, sender, i.getLanguage().getString("invalid-args"))) return;

        if (args[0].equalsIgnoreCase("reload")) {

            if (!checkHasPermission("rankgrantplus.reload", sender,
                    i.getLanguage().getString("no-perms"))) return;

            i.config.reloadConfig();
            i.language.reloadConfig();
            i.ranks.reloadConfig();

            MessageUtils.sendMessageToCommandSender(sender, i.getLanguage().getString("reloaded"));
        }
    }
}
