package com.demeng7215.rankgrantplus.inventories;

import com.demeng7215.demapi.api.MessageUtils;
import com.demeng7215.rankgrantplus.utils.DurationUtils;
import com.demeng7215.rankgrantplus.utils.RGPInventory;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.mineacademy.remain.model.CompMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DurationChooseInv extends RGPInventory {

    private final RankGrantPlus i;

    private DurationUtils currentDurationUtil;

    private static int taskId;

    DurationChooseInv(RankGrantPlus i, OfflinePlayer target, Player op, String rank) {
        super(54, MessageUtils.color(i.getLanguage().getString("gui-names.choose-time")
                .replace("%target%", target.getName())));
        this.i = i;

        List<String> times = Arrays.asList("add-second", "add-minute", "add-hour", "add-day", "add-week", "subtract-second",
                "subtract-minute", "subtract-hour", "subtract-day", "subtract-week", "permanent");

        for (String duration : times) {

            String path = "duration." + duration + ".";

            this.currentDurationUtil = new DurationUtils(0L);


            taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(i, () -> {

                List<String> continueLore = new ArrayList<>();
                for (String lore : i.getConfiguration().getStringList("duration.continue.lore")) {
                    continueLore.add(replaceInfo(lore, rank, target));
                }

                setItem(i.getConfiguration().getInt("duration.continue.slot") - 1,
                        CompMaterial.valueOf(i.getConfiguration()
                                .getString("duration.continue.item")).toItem(),
                        i.getConfiguration().getString("duration.continue.name"),
                        continueLore, player -> new ReasonSelectInv(i, target, op, rank, currentDurationUtil).open(op));

                List<String> finalLore = new ArrayList<>();
                for (String lore : i.getConfiguration().getStringList(path + "lore")) {
                    finalLore.add(replaceInfo(lore, rank, target));
                }
                setItem(i.getConfiguration().getInt(path + "slot") - 1,
                        CompMaterial.valueOf(i.getConfiguration().getString(path + "item"))
                                .toItem(),
                        replaceInfo(i.getConfiguration().getString(path + "name"), rank, target),
                        finalLore, player -> {

                            if (duration.contains("add")) {
                                if (duration.contains("week")) addSeconds(604800L);
                                if (duration.contains("day")) addSeconds(86400L);
                                if (duration.contains("hour")) addSeconds(3600L);
                                if (duration.contains("minute")) addSeconds(60L);
                                if (duration.contains("second")) addSeconds(1L);
                                return;
                            }

                            if (duration.contains("subtract")) {
                                if (duration.contains("week")) addSeconds(-604800L);
                                if (duration.contains("day")) addSeconds(-86400L);
                                if (duration.contains("hour")) addSeconds(-3600L);
                                if (duration.contains("minute")) addSeconds(-60L);
                                if (duration.contains("second")) addSeconds(-1L);
                            }

                            if (duration.equals("permanent")) {
                                currentDurationUtil.setPermanent();
                                new ReasonSelectInv(i, target, op, rank, currentDurationUtil).open(op);
                            }
                        });
            }, 0L, 5L);
        }
    }

    private long durationInSeconds = 0;

    private void addSeconds(long seconds) {
        if (seconds < 0 && durationInSeconds + seconds < 0) {
            durationInSeconds = 0;
            currentDurationUtil = new DurationUtils(0);
            return;
        }

        durationInSeconds = durationInSeconds + seconds;
        currentDurationUtil = new DurationUtils(durationInSeconds);
    }

    private String replaceInfo(String s, String rank, OfflinePlayer target) {

        String rankName;

        if (i.getRanks().getString("ranks." + rank + ".name") == null) {
            rankName = rank;
        } else {
            rankName = RankGrantPlus.stripColorCodes(i.getRanks().getString("ranks." + rank + ".name"));
        }

        String duration;
        if (currentDurationUtil.isPermanent()) {
            duration = i.getConfiguration().getString("duration.word-permanent");
        } else {
            duration = replaceTimes(i.getConfiguration().getString("duration.duration-format"));
        }

        return s.replace("%rank%", rankName)
                .replace("%target%", target.getName())
                .replace("%duration%",
                        MessageUtils.color(duration));
    }

    private String replaceTimes(String s) {
        return s.replace("%weeks%", this.currentDurationUtil.getWeeks())
                .replace("%days%", this.currentDurationUtil.getDays())
                .replace("%hours%", this.currentDurationUtil.getHours())
                .replace("%minutes%", this.currentDurationUtil.getMinutes())
                .replace("%seconds%", this.currentDurationUtil.getSeconds());
    }

    public static int getTaskId() {
        return taskId;
    }
}
