package dev.demeng.rankgrantplus.util;

import dev.demeng.pluginbase.TimeUtils;
import dev.demeng.pluginbase.TimeUtils.DurationFormatter;
import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.rankgrantplus.RankGrantPlus;

/**
 * Common utilities.
 */
public class Utils {

  /**
   * Gets the stripped display name of the rank.
   *
   * @param rank The rank ID
   * @return The stripped display name of the rank
   */
  public static String getRankName(String rank) {
    return ChatUtils.strip(
        RankGrantPlus.getInstance().getRanks().getString("ranks." + rank + ".display-name"));
  }

  /**
   * Gets the stripped display name of the reason.
   *
   * @param reason The reason ID
   * @return The stripped display name of the reason
   */
  public static String getReasonName(String reason) {
    return ChatUtils.strip(
        RankGrantPlus.getInstance().getSettings()
            .getString("menus.reason-select.reasons." + reason + ".display-name"));
  }

  /**
   * Formats a duration.
   *
   * @param duration The duration in SECONDS
   * @return The formatted duration
   */
  public static String formatDuration(long duration) {
    return duration == 0 ? "Permanent"
        : TimeUtils.formatDuration(DurationFormatter.LONG, duration * 1000);
  }
}
