package com.xtra.api.projection.admin.collection;

public interface CollectionSimpleView {
    Long getId();

    String getName();

    Integer getChannelCount();

    Integer getMovieCount();

    Integer getSeriesCount();

    Integer getRadioCount();
}
