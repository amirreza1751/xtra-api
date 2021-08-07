package com.xtra.api.projection.admin.connection;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class VodConnectionResult implements Comparable<VodConnectionResult> {
    private long serverId;
    private Long count = Long.MAX_VALUE;

    public VodConnectionResult(long serverId, long count) {
        this.serverId = serverId;
        this.count = count;
    }

    public VodConnectionResult(long serverId) {
        this.serverId = serverId;
    }

    @Override
    public int compareTo(VodConnectionResult vodConnectionResult) {
        return this.count.compareTo(vodConnectionResult.getCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VodConnectionResult that = (VodConnectionResult) o;
        return getServerId() == that.getServerId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerId(), getCount());
    }
}
