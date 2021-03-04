package com.xtra.api.projection.reseller.subreseller;

import lombok.Data;

@Data
public class CreditChangeRequest {
    private int changeAmount;
    private String description;
}
