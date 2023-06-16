/*
 * MIT License
 *
 * Copyright (c) 2023 Demeng Chen
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

import dev.demeng.pluginbase.DynamicPlaceholders;
import dev.demeng.pluginbase.menu.model.MenuButton;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.util.ConfigMenu;
import dev.demeng.rankgrantplus.util.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * The menu for selecting the grant duration.
 */
public class DurationSelectMenu extends ConfigMenu {

  // The map of the duration options and its equivalent value in seconds.
  private static final Map<String, Long> OPTIONS = new HashMap<>();

  // The rate limit in milliseconds before duration updates.
  private static final long RATE_LIMIT = 180L;

  static {
    OPTIONS.put("add-minute", 60L);
    OPTIONS.put("add-hour", 3600L);
    OPTIONS.put("add-day", 86400L);
    OPTIONS.put("add-week", 604800L);

    OPTIONS.put("subtract-minute", -60L);
    OPTIONS.put("subtract-hour", -3600L);
    OPTIONS.put("subtract-day", -86400L);
    OPTIONS.put("subtract-week", -604800L);
  }

  private final Map<Long, MenuButton> optionButtons = new HashMap<>();

  private final RankGrantPlus i;
  private final Player issuer;
  private final OfflinePlayer target;
  private final String rank;

  private long currentSeconds = 0;
  private final Placeholders placeholders;
  private long lastClicked;

  DurationSelectMenu(RankGrantPlus i, Player issuer, OfflinePlayer target, String rank) {
    super(i, "duration-select", str -> str.replace("%target%",
        Objects.requireNonNull(target.getName(), "Target name is null")));

    this.i = i;
    this.issuer = issuer;
    this.target = target;
    this.rank = rank;
    this.placeholders = new Placeholders(
        target.getName(), Utils.getRankName(rank), "Permanent");

    for (Map.Entry<String, Long> option : OPTIONS.entrySet()) {

      final ConfigurationSection section = i.getSettings()
          .getConfigurationSection("menus.duration-select." + option.getKey());

      if (section != null) {
        optionButtons.put(option.getValue(), MenuButton.create(section, null));
      }
    }

    reload();
  }

  private void reload() {

    for (Map.Entry<Long, MenuButton> entry : optionButtons.entrySet()) {
      addButton(MenuButton.create(entry.getValue().getSlot(),
          placeholders.replace(entry.getValue().getStack()), event -> {
            updateCurrentDuration(entry.getKey());
            reload();
            issuer.updateInventory();
          }));
    }

    addButton(MenuButton.create(Objects.requireNonNull(
            i.getSettings().getConfigurationSection("menus.duration-select.continue")),
        placeholders.toOperator(),
        event -> new ReasonSelectMenu(i, issuer, target, rank, currentSeconds).open(issuer)));
  }

  private void updateCurrentDuration(long secondsToAdd) {

    if (lastClicked + RATE_LIMIT > System.currentTimeMillis()) {
      return;
    }

    lastClicked = System.currentTimeMillis();

    currentSeconds += secondsToAdd;

    if (currentSeconds < 0) {
      currentSeconds = 0;
    }

    placeholders.setDuration(Utils.formatDuration(currentSeconds));
  }

  @Data
  @AllArgsConstructor
  public class Placeholders implements DynamicPlaceholders {

    private String target;
    private String rank;
    private String duration;

    @Override
    public String setPlaceholders(String str) {
      return str
          .replace("%target%", target)
          .replace("%rank%", rank)
          .replace("%duration%", duration);
    }
  }
}
