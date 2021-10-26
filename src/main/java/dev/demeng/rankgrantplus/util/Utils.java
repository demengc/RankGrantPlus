package dev.demeng.rankgrantplus.util;

import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.rankgrantplus.RankGrantPlus;

/**
 * Common utilities.
 */
public class Utils {

  public static String getRankName(String rank) {
    return ChatUtils.strip(
        RankGrantPlus.getInstance().getRanks().getString("ranks." + rank + ".display-name"));
  }

  public static String getReasonName(String reason) {
    return ChatUtils.strip(
        RankGrantPlus.getInstance().getSettings()
            .getString("menus.reason-select.reasons." + reason + ".display-name"));
  }
}
