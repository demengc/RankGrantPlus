package dev.demeng.rankgrantplus.utils;

import lombok.Getter;

public class Duration {

  @Getter private final long totalSeconds;

  @Getter private long weeks = 0;
  @Getter private long days = 0;
  @Getter private long hours = 0;
  @Getter private long minutes = 0;
  @Getter private long seconds = 0;
  @Getter private boolean permanent = false;

  public Duration(long totalSeconds) {
    this.totalSeconds = totalSeconds;

    if (totalSeconds == -1) {
      this.permanent = true;
      return;
    }

    this.weeks = totalSeconds / 604800;
    this.days = (totalSeconds % 604800) / 86400;
    this.hours = ((totalSeconds % 604800) % 86400) / 3600;
    this.minutes = (((totalSeconds % 604800) % 86400) % 3600) / 60;
    this.seconds = (((totalSeconds % 604800) % 86400) % 3600) % 60;
  }

  public String replaceTimes(String s) {
    return s.replace("%weeks%", "" + weeks)
        .replace("%days%", "" + days)
        .replace("%hours%", "" + hours)
        .replace("%minutes%", "" + minutes)
        .replace("%seconds%", "" + seconds);
  }
}
