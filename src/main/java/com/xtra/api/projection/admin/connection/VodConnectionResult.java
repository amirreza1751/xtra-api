package com.xtra.api.projection.admin.connection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VodConnectionResult implements Comparable<VodConnectionResult> {
    private long serverId;
    private Long count = Long.MAX_VALUE;

    public VodConnectionResult(long serverId, long count) {
        this.serverId = serverId;
        this.count = count;
    }

    @Override
    public int compareTo(VodConnectionResult vodConnectionResult) {
        return this.count.compareTo(vodConnectionResult.getCount());
    }
}
