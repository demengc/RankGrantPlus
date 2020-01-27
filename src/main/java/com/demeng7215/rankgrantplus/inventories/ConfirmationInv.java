package com.demeng7215.rankgrantplus.inventories;

import com.demeng7215.demlib.api.gui.CustomInventory;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.utils.DurationUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.mineacademy.remain.model.CompMaterial;

import java.util.ArrayList;
import java.util.List;

class ConfirmationInv extends CustomInventory {

    private final RankGrantPlus i;
    private final DurationUtils duration;

    ConfirmationInv(RankGrantPlus i, OfflinePlayer target, Player op,
                    String rank, DurationUtils duration, String displayReason) {
        super(27, MessageUtils.colorize(i.getLang().getString("gui-names.confirm-grant")
                .replace("%target%", target.getName())));

        this.i = i;
        this.duration = duration;

        List<String> confirmLore = new ArrayList<>();
        for (String lore : i.getConfiguration().getStringList("confirmation.confirm.lore")) {
            confirmLore.add(replaceInfo(lore, rank, target, displayReason, op));
        }

        setItem(11,
                CompMaterial.valueOf(i.getConfiguration().getString("confirmation.confirm.item"))
                        .toItem(),
                i.getConfiguration().getString("confirmation.confirm.name"), confirmLore,
                player -> {
                    player.closeInventory();
                    if (grant(target, rank, duration, displayReason, player)) {
                        MessageUtils.tell(op, replaceInfo(i.getConfiguration()
                                .getString("confirmation.confirm.message"), rank, target, displayReason, op));
                    }
                });

        List<String> cancelLore = new ArrayList<>();
        for (String lore : i.getConfiguration().getStringList("confirmation.cancel.lore")) {
            cancelLore.add(replaceInfo(lore, rank, target, displayReason, op));
        }

        setItem(15,
                CompMaterial.valueOf(i.getConfiguration().getString("confirmation.cancel.item"))
                        .toItem(),
                i.getConfiguration().getString("confirmation.cancel.name"), cancelLore,
                player -> {
                    player.closeInventory();
                    MessageUtils.tell(op, replaceInfo(i.getConfiguration()
                            .getString("confirmation.cancel.message"), rank, target, displayReason, op));
                });
    }


    private boolean grant(OfflinePlayer target, String rank, DurationUtils utils, String displayReason, Player op) {
        try {

            for (String cmd : i.getConfiguration().getStringList("commands.grant"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd
                        .replace("%target%", target.getName())
                        .replace("%rank%", rank));

            i.getGrantLogs().log(RankGrantPlus.stripColorCodes(replaceInfo(i.getLang()
                    .getString("log-format"), rank, target, displayReason, op)), true);

            if (!utils.isPermanent()) {
                i.getData().createSection(target.getUniqueId() + "," + rank)
                        .set("remaining", duration.getTotalSeconds());
                i.getData().set(target.getUniqueId() + "," + rank + ".remaining", duration.getTotalSeconds());
                i.dataFile.saveConfig();
            }

            final String message = i.getLang().getString("notification");
            if (!message.equals("none") && target.isOnline())
                MessageUtils.tell((Player) target, replaceInfo(message, rank, target, displayReason, op));

            return true;

        } catch (final Exception ex) {
            ex.printStackTrace();
            MessageUtils.tell(op, i.getLang().getString("failed-grant"));
            return false;
        }
    }


    private String replaceInfo(String s, String rank, OfflinePlayer target, String displayReason, Player op) {

        String rankName;

        if (i.getRanks().getString("ranks." + rank + ".name") == null) {
            rankName = rank;
        } else {
            rankName = RankGrantPlus.stripColorCodes(i.getRanks().getString("ranks." + rank + ".name"));
        }

        String duration;

        if (this.duration.isPermanent()) {
            duration = i.getConfiguration().getString("duration.word-permanent");
        } else {
            duration = replaceTimes(i.getConfiguration().getString("duration.duration-format"));
        }

        return s.replace("%rank%", rankName)
                .replace("%target%", target.getName())
                .replace("%duration%",
                        MessageUtils.colorize(replaceTimes(duration)))
                .replace("%reason%", RankGrantPlus.stripColorCodes(displayReason))
                .replace("%op%", op.getName());
    }

    private String replaceTimes(String s) {
        return s.replace("%weeks%", duration.getWeeks())
                .replace("%days%", this.duration.getDays())
                .replace("%hours%", this.duration.getHours())
                .replace("%minutes%", this.duration.getMinutes())
                .replace("%seconds%", this.duration.getSeconds());
    }
}
