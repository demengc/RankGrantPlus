package com.demeng7215.rankgrantplus.commands;

import com.demeng7215.demlib.api.CustomCommand;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import com.demeng7215.rankgrantplus.inventories.RankSelectInv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GrantCmd extends CustomCommand {

	private final RankGrantPlus i;

	public GrantCmd(RankGrantPlus i) {
		super("grant");

		setDescription("Grant a player a rank.");
		setAliases(Arrays.asList("appoint", "grantrank", "appointrank"));

		this.i = i;
	}

	@Override
	protected void run(CommandSender sender, String[] args) {

		if (!i.isEnabled()) {
			MessageUtils.consoleWithoutPrefix("&cRankGrant+ has already been disabled due to an error " +
					"prior to this. Please resolve that error and try granting again.");
			return;
		}

		if (!checkIsPlayer(sender, i.getLang().getString("console"))) return;

		if (!checkArgsStrict(args, 1, sender, i.getLang().getString("invalid-args"))) return;

		if (!checkHasPerm("rankgrantplus.grant", sender,
				i.getLang().getString("no-perms"))) return;

		final Player p = (Player) sender;

		new RankSelectInv(i, Bukkit.getOfflinePlayer(args[0]), p).open((Player) sender);
	}
}
