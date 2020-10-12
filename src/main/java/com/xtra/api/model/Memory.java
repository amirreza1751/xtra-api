package com.xtra.api.model;

import lombok.Data;


@Data
public class Memory {

    private String description;
    private double total;
    private double available;

    public Memory(String description, double total, double available) {
        this.description = description;
        this.total = total;
        this.available = available;
    }

    public Memory(){}


}
