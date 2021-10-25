package dev.demeng.rankgrantplus.menus;

import dev.demeng.pluginbase.TimeUtils;
import dev.demeng.pluginbase.TimeUtils.DurationFormatter;
import dev.demeng.pluginbase.chat.Placeholders;
import dev.demeng.pluginbase.menu.model.MenuButton;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.util.ConfigMenu;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    OPTIONS.put("add-second", 1L);
    OPTIONS.put("add-minute", 60L);
    OPTIONS.put("add-hour", 3600L);
    OPTIONS.put("add-day", 86400L);
    OPTIONS.put("add-week", 604800L);

    OPTIONS.put("subtract-second", -1L);
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
  private Placeholders placeholders = Placeholders.of("%duration%", "Permanent");
  private long lastClicked;

  public DurationSelectMenu(RankGrantPlus i, Player issuer, OfflinePlayer target, String rank) {
    super(i, "duration-select", Placeholders.of("%target%",
        Objects.requireNonNull(target.getName(), "Target name is null")));

    this.i = i;
    this.issuer = issuer;
    this.target = target;
    this.rank = rank;

    for (Map.Entry<String, Long> option : OPTIONS.entrySet()) {

      final ConfigurationSection section = i.getSettings()
          .getConfigurationSection("menus.duration-select." + option.getKey());

      if (section != null) {
        optionButtons.put(option.getValue(), MenuButton.fromConfig(section, null));
      }
    }

    reload();
  }

  private void reload() {

    for (Map.Entry<Long, MenuButton> entry : optionButtons.entrySet()) {
      addButton(new MenuButton(entry.getValue().getSlot(),
          placeholders.set(entry.getValue().getStack()), event -> {
        updateCurrentDuration(entry.getKey());
        reload();
        issuer.updateInventory();
      }));
    }

    addButton(MenuButton.fromConfig(Objects.requireNonNull(
            i.getSettings().getConfigurationSection("menus.duration-select.continue")),
        placeholders,
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

    placeholders = Placeholders.of("%duration%", currentSeconds == 0
        ? "Permanent"
        : TimeUtils.formatDuration(DurationFormatter.LONG, currentSeconds * 1000));
  }
}
