package com.xtra.api.projection.admin.catchup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatchupRecordView {
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime stop;
    private String streamInput;
    private int catchUpDays = 0;
    private String location;

    public CatchupRecordView(String title, ZonedDateTime start, ZonedDateTime stop, String streamInput, int catchUpDays) {
        this.title = title;
        this.start = start;
        this.stop = stop;
        this.streamInput = streamInput;
        this.catchUpDays = catchUpDays;
    }
}
