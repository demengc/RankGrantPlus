package dev.demeng.rankgrantplus.menus;

import dev.demeng.pluginbase.TimeUtils;
import dev.demeng.pluginbase.TimeUtils.DurationFormatter;
import dev.demeng.pluginbase.chat.Placeholders;
import dev.demeng.pluginbase.menu.model.MenuButton;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.util.ConfigMenu;
import dev.demeng.rankgrantplus.util.Utils;
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

    final Placeholders placeholders = Placeholders
        .of("%target%", Objects.requireNonNull(target.getName()))
        .add("%rank%", Utils.getRankName(rank))
        .add("%duration%", TimeUtils.formatDuration(DurationFormatter.LONG, duration * 1000));

    for (String reason : Objects.requireNonNull(
        i.getSettings().getConfigurationSection("menus.reason-select.reasons"),
        "Reasons section is null").getKeys(false)) {

      final String path = "menus.reason-select.reasons." + reason;
      final String permission = i.getSettings().getString(path + ".permission");

      // Do not display the reason if the issuer does not have the required permission.
      if (permission != null
          && !permission.equalsIgnoreCase("none")
          && !issuer.hasPermission(permission)) {
        continue;
      }

      addButton(MenuButton.fromConfig(
          Objects.requireNonNull(i.getSettings().getConfigurationSection(path)), placeholders,
          event -> new ConfirmMenu(i, issuer, target, rank, duration, reason).open(issuer)));
    }
  }
}
