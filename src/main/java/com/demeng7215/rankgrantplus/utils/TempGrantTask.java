package com.demeng7215.rankgrantplus.utils;

import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TempGrantTask extends BukkitRunnable {

  private final RankGrantPlus i;

  public TempGrantTask(RankGrantPlus i) {
    this.i = i;
  }

  @Override
  public void run() {

    for (String id : i.getData().getKeys(false)) {

      if (!id.equals("do-not-remove")) {

        if (i.getData().getLong(id + ".remaining") <= 0L) {

          String[] args = id.split(",");
          String uuid = args[0];
          String rank = args[1];

          try {

            for (String cmd : i.getSettings().getStringList("commands.ungrant"))
              Bukkit.dispatchCommand(
                  Bukkit.getConsoleSender(),
                  cmd.replace("%target%", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName())
                      .replace("%rank%", rank));

            i.getData().set(id, null);

          } catch (final NullPointerException ignored) {
            i.getData().set(id, null);
          }

          try {
            i.dataFile.saveConfig();
          } catch (final Exception ex) {
            MessageUtils.error(ex, 5, "Failed to save data.", true);
          }
          return;
        }

        i.getData().set(id + ".remaining", i.getData().getLong(id + ".remaining") - 1L);

        try {
          i.dataFile.saveConfig();
        } catch (final Exception ex) {
          MessageUtils.error(ex, 5, "Failed to save data.", true);
        }
      }
    }
  }
}
