package com.xtra.api;

import com.xtra.api.model.CollectionStream;
import com.xtra.api.model.CollectionStreamId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaTest {

    @Test
    void equalsTest() {
        var a = new CollectionStream(new CollectionStreamId(1L, 2L));
        var b = new CollectionStream(new CollectionStreamId(1L, 2L));
        assertEquals(a,b);
    }
}