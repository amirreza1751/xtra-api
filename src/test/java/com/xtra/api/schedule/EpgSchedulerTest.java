package com.xtra.api.schedule;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.xtra.api.model.EpgFileTag;
import com.xtra.api.projection.epg.EpgFileTag;
import com.xtra.api.model.EpgChannel;
import com.xtra.api.model.Program;
import com.xtra.api.repository.EpgChannelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EpgSchedulerTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updateEpg() throws IOException, XMLStreamException {
//        XmlMapper xmlMapper = new XmlMapper();
//        URL xmlUrl = new URL("http://epg.streamstv.me/hidden/guide-norway.xml");
//
//        try {
//            EpgFileTag value = xmlMapper.readValue(xmlUrl.openStream(), EpgFileTag.class);
//            System.out.println("test");
//            List<EpgChannel> epgChannels = new ArrayList<>();
//            value.getEpgChannelTags().forEach(epgChannelTag -> {
//                Set<Program> programs = new HashSet<>();
//                EpgChannel epgChannel1 = new EpgChannel();
//                value.getProgramTags().forEach(programTag -> {
//                    if (programTag.getChannelId().equals(epgChannelTag.getName())){
//                        epgChannel1.setName(epgChannelTag.getName());
//                        com.xtra.api.model.Program program1 = new com.xtra.api.model.Program();
//                        program1.setTitle(programTag.getTitleTag().getText() + "___" + programTag.getChannelId());
//                        programs.add(program1);
//                    }
//                });
//                epgChannel1.setPrograms(programs);
//                epgChannels.add(epgChannel1);
//            });
//            EpgChannel testchannel = epgChannels.get(10);
//            System.out.println("test");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertEquals( 1 , 1);


    }

    @Test
    void dateTime(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss [XXX][X]");
        LocalDateTime dateTime = LocalDateTime.parse("20210110084500 +0200", formatter);

     //   ZonedDateTime zonedDateTime = ZonedDateTime.parse("20210110084500 +0200", formatter);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("20210113001400 -1000", formatter);

    }

    @Test
    void test() throws IOException, XMLStreamException {
//        EpgScheduler epgScheduler = new EpgScheduler(epgChannelRepository);
//        epgScheduler.updateEpg();
//        var programs = epgChannelRepository.findById(1l).get().getPrograms();
//        System.out.println();
    }

}