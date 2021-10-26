package dev.demeng.rankgrantplus.tasks;

import dev.demeng.pluginbase.Common;
import dev.demeng.pluginbase.chat.Placeholders;
import dev.demeng.rankgrantplus.RankGrantPlus;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

/**
 * The runnable responsible for making ranks expire after their given duration.
 */
@RequiredArgsConstructor
public class GrantExpirationTask implements Runnable {

  private final RankGrantPlus i;

  @Override
  public void run() {

    for (String key : Objects.requireNonNull(
        i.getData().getConfigurationSection("temp-grants"),
        "Data section is null").getKeys(false)) {

      final long expiration = i.getData().getLong("temp-grants." + key);

      if (System.currentTimeMillis() < expiration) {
        continue;
      }

      final String[] split = key.split(",");
      //noinspection deprecation
      final String target = Bukkit.getOfflinePlayer(split[0]).getName();
      final String rank = split[1];

      Objects.requireNonNull(target, "Expiration target name is null");

      final Placeholders placeholders = Placeholders
          .of("%target%", target)
          .add("%rank%", rank);

      for (String command : i.getSettings().getStringList("commands.expiration")) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholders.set(command));
      }

      i.getData().set("temp-grants." + key, null);

      try {
        i.getDataFile().save();
      } catch (IOException ex) {
        Common.error(ex, "Failed to save data.", false);
      }
    }
  }
}
