package com.demeng7215.rankgrantplus.utils;

public class DurationUtils {

    private final long totalSeconds;

    private final String weeks;
    private final String days;
    private final String hours;
    private final String minutes;
    private final String seconds;
    private boolean permanent;

    public DurationUtils(long totalSeconds) {
        this.totalSeconds = totalSeconds;
        this.weeks = convertTwoDigits(totalSeconds / 604800);
        this.days = convertTwoDigits((totalSeconds % 604800) / 86400);
        this.hours = convertTwoDigits(((totalSeconds % 604800) % 86400) / 3600);
        this.minutes = convertTwoDigits((((totalSeconds % 604800) % 86400) % 3600) / 60);
        this.seconds = convertTwoDigits((((totalSeconds % 604800) % 86400) % 3600) % 60);
    }

    public long getTotalSeconds() {
        return this.totalSeconds;
    }

    public String getWeeks() {
        return this.weeks;
    }

    public String getDays() {
        return this.days;
    }

    public String getHours() {
        return this.hours;
    }

    public String getMinutes() {
        return this.minutes;
    }

    public String getSeconds() {
        return this.seconds;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent() {
        this.permanent = true;
    }

    private String convertTwoDigits(long l) {

        if (l == 0) {
            return "00";
        }

        if (l / 10 == 0) {
            return "0" + l;
        }

        return String.valueOf(l);
    }
}
