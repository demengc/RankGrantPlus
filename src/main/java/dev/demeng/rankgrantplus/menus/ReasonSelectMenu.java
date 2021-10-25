package dev.demeng.rankgrantplus.menus;

import dev.demeng.pluginbase.chat.Placeholders;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.util.ConfigMenu;
import java.util.Objects;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * The menu for selecting the grant reason.
 */
public class ReasonSelectMenu extends ConfigMenu {

  public ReasonSelectMenu(RankGrantPlus i, Player issuer, OfflinePlayer target, String rank,
      long duration) {
    super(i, "reason-select", Placeholders.of("%target%",
        Objects.requireNonNull(target.getName(), "Target name is null")));

  }
}
