package com.xtra.api.model.user;

import lombok.Getter;

@Getter
public enum ResellerLogAction {
    NEW_LINE("Reseller created new line"),
    EXTEND_LINE("Reseller extended line"),
    BLOCK_LINE("Reseller blocked line"),
    BAN_LINE("Reseller banned line"),
    EDIT_LINE("Reseller edited line"),
    DELETE_LINE("Reseller deleted line"),
    NEW_SUBRESELLER("Reseller created new subreseller"),
    CREDIT_CHANGE_SUBRESELLER("Reseller changed credit of subreseller"),
    DELETE_SUBRESELLER("Reseller deleted subreseller"),
    ;

    private final String text;

    ResellerLogAction(String text) {
        this.text = text;
    }
}
