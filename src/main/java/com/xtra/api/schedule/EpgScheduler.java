package com.xtra.api.schedule;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.xtra.api.model.Collection;
import com.xtra.api.projection.epg.CategoryTag;
import com.xtra.api.projection.epg.EpgChannelTag;
import com.xtra.api.projection.epg.EpgFileTag;
import com.xtra.api.model.EpgChannel;
import com.xtra.api.model.Program;
import com.xtra.api.projection.epg.ProgramTag;
import com.xtra.api.repository.EpgChannelRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EpgScheduler {
    private final EpgChannelRepository epgChannelRepository;

    public EpgScheduler(EpgChannelRepository epgChannelRepository) {
        this.epgChannelRepository = epgChannelRepository;
    }

    @Scheduled(cron = "0 0 * * 1")
//    @Scheduled(fixedDelay = 60000)
    public void updateEpg(){
        System.out.println("test");
        XmlMapper xmlMapper = new XmlMapper();
        URL xmlUrl = null;
        try {
            xmlUrl = new URL("http://epg.streamstv.me/hidden/guide-iran.xml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            EpgFileTag value = xmlMapper.readValue(xmlUrl.openStream(), EpgFileTag.class);
            System.out.println("test");
            List<EpgChannel> epgChannels = new ArrayList<>();
            for (EpgChannelTag epgChannelTag : value.getEpgChannelTags()) {
                EpgChannel epgChannel = convertToEntity(epgChannelTag);
                Set<Program> programs = new HashSet<>();
                for (ProgramTag programTag : value.getProgramTags()) {
                    if (programTag.getChannelId().equals(epgChannelTag.getName())) {
                        Program program = convertToEntity(programTag);
                        programs.add(program);
                    }
                }
                epgChannel.setPrograms(programs);
                epgChannels.add(epgChannel);
            }
            epgChannelRepository.saveAll(epgChannels);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public EpgChannel convertToEntity(EpgChannelTag epgChannelTag){
        EpgChannel epgChannel = new EpgChannel();
        epgChannel.setName(epgChannelTag.getName());
        epgChannel.setIcon(epgChannelTag.getIconTag() == null ? "unknown" : epgChannelTag.getIconTag().getSrc());
        epgChannel.setUrl(epgChannelTag.getUrlTag() == null ? "unknown" :epgChannelTag.getUrlTag().getText());
        epgChannel.setLanguage(epgChannelTag.getDisplayNameTag().getLanguage() == null ? "unknown" :epgChannelTag.getDisplayNameTag().getLanguage());
        return epgChannel;
    }

    public Program convertToEntity(ProgramTag programTag){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss [XXX][X]");
        ZonedDateTime start = ZonedDateTime.parse(programTag.getStart(), formatter);
        ZonedDateTime stop = ZonedDateTime.parse(programTag.getStop(), formatter);

        Program program = new Program();
        program.setTitle(programTag.getTitleTag().getText());
        program.setDescription(programTag.getDescriptionTag() == null ? "unknown" :programTag.getDescriptionTag().getText());
        program.setCategory(programTag.getCategories() == null ? "unknown" : programTag.getCategories().stream().map(CategoryTag::getText).collect(Collectors.joining("|")));
        program.setLanguage(programTag.getLanguage() == null ? "unknown" :programTag.getLanguage());
        program.setStart(start);
        program.setStop(stop);
        return  program;
    }
}
