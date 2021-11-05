package com.xtra.api.model.user;

import lombok.Getter;

@Getter
public enum CreditLogReason {
    ADMIN_MANUAL_CHANGE("Admin Manual Change"),
    RESELLER_CREDIT_TRANSFER("Reseller Credit Transfer"),
    RESELLER_SUBRESELLER_CREATION("Reseller Subreseller Creation"),
    RESELLER_GIFT_CARD("Reseller Gift Card"),
    RESELLER_LINE_CREATE_EXTEND("Reseller Line Create/Extend");

    private final String text;

    CreditLogReason(String text) {
        this.text = text;
    }
}
