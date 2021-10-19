package com.xtra.api.model.vod;

import lombok.Getter;

@Getter
public enum Resolution {
    RES_480P("480p"),
    RES_576P("576p"),
    RES_720P("720p"),
    RES_1080P("1080p"),
    RES_4K_UHD("4k UHD");

    private final String text;

    Resolution(String text) {
        this.text = text;
    }
}
