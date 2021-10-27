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

package dev.demeng.rankgrantplus.util;

import dev.demeng.pluginbase.TimeUtils;
import dev.demeng.pluginbase.TimeUtils.DurationFormatter;
import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.rankgrantplus.RankGrantPlus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Common utilities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {

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
