/*
 * MIT License
 *
 * Copyright (c) 2018-2022 Demeng Chen
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

package dev.demeng.rankgrantplus.menus;

import dev.demeng.pluginbase.chat.Placeholders;
import dev.demeng.pluginbase.menu.model.MenuButton;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.util.ConfigMenu;
import dev.demeng.rankgrantplus.util.Utils;
import java.util.Objects;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * The menu for selecting the rank to grant.
 */
public class RankSelectMenu extends ConfigMenu {

  public RankSelectMenu(RankGrantPlus i, Player issuer, OfflinePlayer target) {
    super(i, "rank-select", Placeholders.of("%target%",
        Objects.requireNonNull(target.getName(), "Target name is null")));

    for (String rank : Objects.requireNonNull(
            i.getRanks().getConfigurationSection("ranks"), "Ranks section is null")
        .getKeys(false)) {

      final String permission = i.getRanks().getString("ranks." + rank + ".permission");

      // Do not display the rank if the issuer does not have the required permission.
      if (permission != null
          && !permission.equalsIgnoreCase("none")
          && !issuer.hasPermission(permission)) {
        continue;
      }

      addButton(MenuButton.fromConfig(
          Objects.requireNonNull(i.getRanks().getConfigurationSection("ranks." + rank)),
          Placeholders
              .of("%target%", target.getName())
              .add("%rank%", Utils.getRankName(rank)),
          event -> new DurationSelectMenu(i, issuer, target, rank).open(issuer)));
    }
  }
}
