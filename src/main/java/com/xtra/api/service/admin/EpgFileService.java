package com.xtra.api.service.admin;


import com.xtra.api.mapper.admin.EpgMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.epg.EpgInsertView;
import com.xtra.api.projection.admin.epg.EpgSimpleView;
import com.xtra.api.projection.admin.epg.EpgView;
import com.xtra.api.repository.EpgChannelRepository;
import com.xtra.api.repository.EpgFileRepository;
import com.xtra.api.service.CrudService;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Service
public class EpgFileService extends CrudService<EpgFile, Long, EpgFileRepository> {

    private final EpgMapper epgMapper;
    private final EpgChannelRepository epgChannelRepository;
    private final EpgFileRepository epgFileRepository;

    @Autowired
    protected EpgFileService(EpgFileRepository epgFileRepository, EpgMapper epgMapper, EpgChannelRepository epgChannelRepository1, EpgFileRepository epgFileRepository1) {
        super(epgFileRepository, "Epg File");
        this.epgMapper = epgMapper;
        this.epgChannelRepository = epgChannelRepository1;
        this.epgFileRepository = epgFileRepository1;
    }

    @Override
    protected Page<EpgFile> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Page<EpgSimpleView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(epgMapper::toSimpleView);
    }

    public EpgView updateEpgFile(Long id, EpgInsertView insertView) {
        return epgMapper.toView(updateOrFail(id, epgMapper.toEntity(insertView)));
    }

    @Override
    public EpgFile updateOrFail(Long id, EpgFile epgFile) {
        var oldEpg = findByIdOrFail(id);
        oldEpg.setName(epgFile.getName());
        oldEpg.setSource(epgFile.getSource());
        return repository.save(oldEpg);
    }

    public EpgView add(EpgInsertView insertView) {
        return epgMapper.toView(repository.save(epgMapper.toEntity(insertView)));
    }

    public EpgView getById(Long id) {
        return epgMapper.toView(findByIdOrFail(id));
    }

    public void syncAllEpg() {
        URL xmlUrl;
        try {
            List<EpgFile> epgFiles = repository.findAll();
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
                if (!StringUtils.isEmpty(epgFile.getLastVersionHash()) && epgFile.getLastVersionHash().equals(newHash))
                    continue;

                epgFile.setLastVersionHash(newHash);
                syncEpg(epgFile, xml);

            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void syncEpg(EpgFile epgFile, String xml) {
        JSONObject root = XML.toJSONObject(xml);
        Set<EpgChannel> epgChannelList = new HashSet<>();
        for (var json : root.getJSONObject("tv").getJSONArray("channel")) {
            var channel = ((JSONObject) json);
            String channelName = channel.getString("id");
            String language = channel.getJSONObject("display-name").getString("lang");
            EpgChannel epgChannel = epgChannelRepository.findByNameAndLanguageAndEpgFile_Id(channelName, language, epgFile.getId())
                    .orElseGet(() -> new EpgChannel(channelName, language, epgFile));
            epgChannel.setUrl(channel.getString("url"));
            if (channel.has("icon"))
                epgChannel.setIcon(channel.getJSONObject("icon").getString("src"));
            epgChannelList.add(epgChannel);
        }
        epgFile.getEpgChannels().retainAll(epgChannelList);
        epgFile.getEpgChannels().addAll(epgChannelList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss [XXX][X]");

        for (var programJson : root.getJSONObject("tv").getJSONArray("programme")) {
            var programObject = ((JSONObject) programJson);
            var channelName = programObject.getString("channel");
            var channel = epgChannelList.stream().filter(epgChannel -> epgChannel.getName().equals(channelName)).findAny();
            if (channel.isPresent()) {
                String title;
                if (programObject.get("title") instanceof JSONArray)
                    title = ((JSONArray) programObject.get("title")).get(0).toString();
                else{
                    System.out.println(programObject.getJSONObject("title").toString());
                    title = programObject.getJSONObject("title").get("content").toString();
                }

                EpgProgram epgProgram = new EpgProgram(new ProgramId(title, ZonedDateTime.parse(programObject.getString("start"), formatter),
                        ZonedDateTime.parse(programObject.getString("stop"), formatter), channel.get().getId()));
                epgProgram.setContent(programJson.toString());
                epgProgram.setEpgChannel(channel.get());
                if (!channel.get().addProgram(epgProgram)) {
                    channel.get().removeProgram(epgProgram);
                    channel.get().addProgram(epgProgram);
                }

            }
        }
        epgFileRepository.save(epgFile);
    }
}
