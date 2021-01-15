package com.xtra.api;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class JavaTest {

//    @Test
//    void equalsTest() {
//        var a = new CollectionStream(new CollectionStreamId(1L, 2L));
//        var b = new CollectionStream(new CollectionStreamId(1L, 2L));
//        assertEquals(a,b);
//    }

    /*@Test
    void testZonedDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss [XXX][X]");
        ZonedDateTime start = ZonedDateTime.parse("20210115122700 +0330", formatter);
        System.out.println(start.toString());
    }*/
}
