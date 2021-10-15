package dev.demeng.rankgrantplus.menus;

import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.pluginbase.chat.Placeholders;
import dev.demeng.pluginbase.menu.model.MenuButton;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.util.ConfigMenu;
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
          Placeholders.of("%target%", target.getName()).add("%rank%", rank),
          event -> ChatUtils.log("Selected rank: " + rank)));
    }
  }
}
