package ga.demeng7215.rankgrantplus.inventories;

import ga.demeng7215.demapi.api.MessageUtils;
import ga.demeng7215.rankgrantplus.RankGrantPlus;
import ga.demeng7215.rankgrantplus.utils.DurationUtils;
import ga.demeng7215.rankgrantplus.utils.RGPInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.remain.model.CompMaterial;

import java.util.ArrayList;
import java.util.List;

class ConfirmationInv extends RGPInventory {

    private final RankGrantPlus i;
    private final DurationUtils duration;

    ConfirmationInv(RankGrantPlus i, OfflinePlayer target, Player op,
                    String rank, DurationUtils duration, String displayReason) {
        super(27, MessageUtils.color(i.getLanguage().getString("gui-names.confirm-grant")
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
                        MessageUtils.sendMessageToPlayer(replaceInfo(i.getConfiguration()
                                .getString("confirmation.confirm.message"), rank, target, displayReason, op), op);
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
                    MessageUtils.sendMessageToPlayer(replaceInfo(i.getConfiguration()
                            .getString("confirmation.cancel.message"), rank, target, displayReason, op), op);
                });
    }


    private boolean grant(OfflinePlayer target, String rank, DurationUtils utils, String displayReason, Player op) {
        try {

            for (String cmd : i.getConfiguration().getStringList("commands.grant"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd
                        .replace("%target%", target.getName())
                        .replace("%rank%", rank));

            i.getGrantLogs().log(RankGrantPlus.stripColorCodes(replaceInfo(i.getLanguage()
                    .getString("log-format"), rank, target, displayReason, op)), true);

            if (!utils.isPermanent()) {
                i.getData().createSection(target.getUniqueId() + "," + rank)
                        .set("remaining", duration.getTotalSeconds());
                i.getData().set(target.getUniqueId() + "," + rank + ".remaining", duration.getTotalSeconds());
                i.data.saveConfig();
            }

            return true;

        } catch (final Exception ex) {
            MessageUtils.sendMessageToPlayer(i.getConfiguration().getString("failed-grant"), op);
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
                        MessageUtils.color(replaceTimes(duration)))
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
