package com.xtra.api.model.user;

import lombok.Getter;

@Getter
public enum ResellerLogAction {
    NEW_LINE("Reseller created new line"),
    EXTEND_LINE("Reseller extended line"),
    EDIT_LINE("Reseller edited line"),
    DELETE_LINE("Reseller deleted line");

    private final String text;

    ResellerLogAction(String text) {
        this.text = text;
    }
}
