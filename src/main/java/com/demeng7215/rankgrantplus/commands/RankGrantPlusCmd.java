package com.demeng7215.rankgrantplus.commands;

import com.demeng7215.demlib.api.Common;
import com.demeng7215.demlib.api.CustomCommand;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class RankGrantPlusCmd extends CustomCommand {

	private final RankGrantPlus i;

	public RankGrantPlusCmd(RankGrantPlus i) {
		super("rankgrantplus");

		setDescription("Main command of RankGrant+.");
		setAliases(Arrays.asList("rankgrant+", "rankgrant"));

		this.i = i;
	}

	@Override
	protected void run(CommandSender sender, String[] args) {

		if (args.length == 0) {
			MessageUtils.tellWithoutPrefix(sender, "&2Running RankGrant+ v" + Common.getVersion() +
					" by Demeng7215.");
			MessageUtils.tellWithoutPrefix(sender, "&fhttps://spigotmc.org/resources/63403/");
			MessageUtils.tellWithoutPrefix(sender, "&aType &f/grant <player> &ato grant a rank.");
			return;
		}

		if (!checkArgsStrict(args, 1, sender, i.getLang().getString("invalid-args"))) return;

		if (args[0].equalsIgnoreCase("reload")) {

			if (!checkHasPerm("rankgrantplus.reload", sender, i.getLang().getString("no-perms"))) return;

			i.configFile.reloadConfig();
			i.languageFile.reloadConfig();
			i.ranksFile.reloadConfig();

			MessageUtils.tell(sender, i.getLang().getString("reloaded"));
		}
	}
}
