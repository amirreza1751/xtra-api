package com.xtra.api.schedule;

import com.xtra.api.model.*;
import com.xtra.api.repository.EpgChannelRepository;
import com.xtra.api.repository.EpgFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
public class EpgScheduler {
    private final EpgChannelRepository epgChannelRepository;
    private final EpgFileRepository epgFileRepository;

    public EpgScheduler(EpgChannelRepository epgChannelRepository, EpgFileRepository epgFileRepository) {
        this.epgChannelRepository = epgChannelRepository;
        this.epgFileRepository = epgFileRepository;
    }

    //@todo dynamic time
    @Scheduled(cron = "0 0 1 * * MON")
    public void checkEpgUpdate() {
        URL xmlUrl;
        try {
            List<EpgFile> epgFiles = epgFileRepository.findAll();
            for (EpgFile epgFile : epgFiles) {
                xmlUrl = new URL(epgFile.getSource());
                String xml;
                try (Scanner scanner = new Scanner(xmlUrl.openStream(),
                        StandardCharsets.UTF_8.toString())) {
                    scanner.useDelimiter("\\A");
                    xml = scanner.hasNext() ? scanner.next() : "";
                }

                //Check if file is updated
                var newHash = DigestUtils.md5Hex(xml);
                if (!epgFile.getLastVersionHash().isEmpty() && epgFile.getLastVersionHash().equals(newHash))
                    continue;

                updateEpg(epgFile, xml);

            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void updateEpg(EpgFile epgFile, String xml) {
        JSONObject root = XML.toJSONObject(xml);
        Set<EpgChannel> epgChannelList = new HashSet<>();
        for (var json : root.getJSONObject("tv").getJSONArray("channel")) {
            var channel = ((JSONObject) json);
            var channelId = new EpgChannelId(channel.getString("id"), channel.getJSONObject("display-name").getString("lang"));
            var dbChannel = epgChannelRepository.findById(channelId);
            EpgChannel epgChannel;
            epgChannel = dbChannel.orElseGet(() -> new EpgChannel(channelId));
            epgChannel.setUrl(channel.getString("url"));
            if (channel.has("icon"))
                epgChannel.setIcon(channel.getJSONObject("icon").getString("src"));
            epgChannel.setEpgFile(epgFile);
            epgChannelList.add(epgChannel);
        }
        epgFile.setEpgChannels(epgChannelList);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss [XXX][X]");

        for (var programJson : root.getJSONObject("tv").getJSONArray("programme")) {
            var programObject = ((JSONObject) programJson);
            var channelName = programObject.getString("channel");
            var channel = epgChannelList.stream().filter(epgChannel -> epgChannel.getId().getName().equals(channelName)).findAny();
            if (channel.isPresent()) {
                Program program = new Program(new ProgramId(programObject.getJSONObject("title").getString("content"), ZonedDateTime.parse(programObject.getString("start"), formatter),
                        ZonedDateTime.parse(programObject.getString("stop"), formatter), programObject.getJSONObject("title").getString("lang")));
                program.setContent(programJson.toString());
                program.setEpgChannel(channel.get());
                if (!channel.get().addProgram(program)) {
                    channel.get().removeProgram(program);
                    channel.get().addProgram(program);
                }

            }
        }
        epgFileRepository.save(epgFile);
    }

}
