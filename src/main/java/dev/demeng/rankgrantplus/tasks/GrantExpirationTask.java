/*
 * MIT License
 *
 * Copyright (c) 2021 Demeng Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
