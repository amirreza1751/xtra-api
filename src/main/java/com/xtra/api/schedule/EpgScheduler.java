package com.xtra.api.schedule;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.xtra.api.model.EpgChannel;
import com.xtra.api.model.EpgFile;
import com.xtra.api.model.Program;
import com.xtra.api.model.ProgramId;
import com.xtra.api.projection.epg.CategoryTag;
import com.xtra.api.projection.epg.EpgChannelTag;
import com.xtra.api.projection.epg.EpgFileTag;
import com.xtra.api.projection.epg.ProgramTag;
import com.xtra.api.repository.EpgChannelRepository;
import com.xtra.api.repository.EpgFileRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Component
public class EpgScheduler {
    private final EpgChannelRepository epgChannelRepository;
    private final EpgFileRepository epgFileRepository;

    public EpgScheduler(EpgChannelRepository epgChannelRepository, EpgFileRepository epgFileRepository) {
        this.epgChannelRepository = epgChannelRepository;
        this.epgFileRepository = epgFileRepository;
    }

    //        @Scheduled(fixedDelay = 2000)
    @Transactional
    @Scheduled(cron = "0 0 1 * * MON")
    public void updateEpg() {
        XmlMapper xmlMapper = new XmlMapper();
        URL xmlUrl;
        try {
            List<EpgFile> epgFiles = epgFileRepository.findAll();
            for (EpgFile epgFile : epgFiles) {
                xmlUrl = new URL(epgFile.getSource());
                EpgFileTag value = xmlMapper.readValue(xmlUrl.openStream(), EpgFileTag.class);
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
                for (EpgChannel epgChannel : epgChannels) {
                    var existingEpgChannel = epgChannelRepository.findByName(epgChannel.getName());
                    if (existingEpgChannel.isEmpty()) {
                        epgChannel.setEpgFile(epgFile);
                        Set<Program> programs = new HashSet<>();
                        for (Program program : epgChannel.getPrograms()) {
                            program.setEpgChannel(epgChannel);
                            programs.add(program);
                        }
                        epgChannel.setPrograms(programs);
                        Set<EpgChannel> channels = epgFile.getEpgChannels();
                        channels.add(epgChannel);
                        epgFile.setEpgChannels(channels);
                        epgChannelRepository.save(epgChannel);
                    } else {
                        var oldEpgChannel = existingEpgChannel.get();
                        copyProperties(epgChannel, oldEpgChannel, "id", "epgFile", "stream", "programs");
                        Set<Program> oldPrograms = oldEpgChannel.getPrograms();
                        Set<Program> programs = new HashSet<>();
                        for (Program program : epgChannel.getPrograms()) {
                            program.setEpgChannel(oldEpgChannel);
                            program.setId(new ProgramId(program.getId().getTitle(), program.getId().getStart(), program.getId().getStop(), program.getId().getLanguage(), oldEpgChannel.getId()));
                            programs.add(program);
                        }
                        oldPrograms.addAll(programs);
                        oldEpgChannel.setPrograms(oldPrograms);
                        oldEpgChannel.setEpgFile(epgFile);
                        epgChannelRepository.save(oldEpgChannel);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public EpgChannel convertToEntity(EpgChannelTag epgChannelTag) {
        EpgChannel epgChannel = new EpgChannel();
        epgChannel.setName(epgChannelTag.getName());
        epgChannel.setIcon(epgChannelTag.getIconTag() == null ? "" : epgChannelTag.getIconTag().getSrc());
        epgChannel.setUrl(epgChannelTag.getUrlTag() == null ? "" : epgChannelTag.getUrlTag().getText());
        epgChannel.setLanguage(epgChannelTag.getDisplayNameTag().getLanguage() == null ? "" : epgChannelTag.getDisplayNameTag().getLanguage());
        return epgChannel;
    }

    public Program convertToEntity(ProgramTag programTag) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss [XXX][X]");
        ZonedDateTime start = ZonedDateTime.parse(programTag.getStart(), formatter);

        ZonedDateTime stop = ZonedDateTime.parse(programTag.getStop(), formatter);

        Program program = new Program();
        program.setId(new ProgramId(programTag.getTitleTag().getText(), start, stop, programTag.getTitleTag().getLang(), null));
        program.setDescription(programTag.getDescriptionTag() == null ? "" : programTag.getDescriptionTag().getText());
        program.setCategory(programTag.getCategories() == null ? "" : programTag.getCategories().stream().map(CategoryTag::getText).collect(Collectors.joining("|")));
        return program;
    }
}
