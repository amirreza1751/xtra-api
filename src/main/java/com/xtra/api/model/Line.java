package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Line extends User {
    private LocalDateTime expireDate;
    private int maxConnections = 1;
    private boolean isReStreamer = false;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isAdminBlocked = false;
    private boolean isIspLocked = false;
    private boolean isStalker;
    private String notes;

    @ManyToOne
    private User referrer;
    @ManyToOne
    private User reseller;


}
