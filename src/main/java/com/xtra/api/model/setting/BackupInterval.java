package com.xtra.api.model.setting;

import lombok.Getter;

@Getter
public enum BackupInterval {
    NONE("none"),
    HOURLY("hourly"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly");

    private final String text;

    BackupInterval(String text) {
        this.text = text;
    }

    public static BackupInterval fromString(String text) {
        for (var interval : BackupInterval.values()) {
            if (interval.text.equalsIgnoreCase(text)) {
                return interval;
            }
        }
        return null;
    }
}
