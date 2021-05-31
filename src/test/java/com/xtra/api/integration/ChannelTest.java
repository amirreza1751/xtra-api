package com.xtra.api.integration;

import com.neovisionaries.i18n.CountryCode;
import com.xtra.api.controller.admin.ChannelController;
import com.xtra.api.mapper.admin.ServerMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.server.ServerInsertView;
import com.xtra.api.projection.admin.server.ServerView;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.service.LineService;
import com.xtra.api.service.admin.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ChannelTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    ResellerService resellerService;
    @Autowired
    RoleService roleService;
    @Autowired
    AdminLineServiceImpl lineService;
    @Autowired
    ChannelService channelService;
    @Autowired
    ServerService serverService;
    @Autowired
    ServerMapper serverMapper;

    @Test
    public void getChannels() throws Exception {
        this.mockMvc.perform(get("/channels")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("content")))
                .andExpect(content().contentType("application/json"))
        ;
    }

    @Test
    public void insertChannel() throws Exception {

        JSONArray stream_inputs = new JSONArray();
        stream_inputs.put("http://test.com");

        JSONObject channel = new JSONObject();
        channel.put("name", "amirakk25");
        channel.put("logo", "http://tes.com");
        channel.put("stream_inputs", stream_inputs);

        this.mockMvc.perform(post("/channels").content(channel.toString()).contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
        ;
    }

    @Test
    public void testInsertChannelValidation() throws Exception {

        JSONArray stream_inputs = new JSONArray();
        stream_inputs.put("http://test.com");

        JSONObject channel = new JSONObject();
        channel.put("name", "test");
        channel.put("logo", "http://tes.com");
        channel.put("stream_inputs", stream_inputs);

        this.mockMvc.perform(post("/channels").content(channel.toString()).contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
        ;
    }

    @Test
    public void updateChannel() throws Exception {
        Channel channel = new Channel();
        channel.setName("test1");
        var savedChannel = channelRepository.save(channel);
        Long id = savedChannel.getId();

        savedChannel.setName("test2");
        JSONObject jo = new JSONObject(savedChannel);


        this.mockMvc.perform(patch("/channels/" + id).content(jo.toString()).contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(containsString("test2")))
        ;
    }

    @Test
    public void deleteChannel() throws Exception {
        Channel channel = new Channel();
        channel.setName("Test Channel");
        var savedChannel = channelRepository.save(channel);
        Long id = savedChannel.getId();

        this.mockMvc.perform(delete("/channels/" + id).contentType("application/json")).andDo(print())
                .andExpect(status().is2xxSuccessful());
        assertThat(channelRepository.findById(id).isEmpty());
    }

    @Test
    public void startOnDemand() throws Exception {
        Reseller reseller = new Reseller();
        reseller.setUsername("reseller");
        reseller.setPassword("reseller");
        reseller.setEmail("reseller@test.com");
        reseller.setCredits(1);
        reseller.setRole(roleService.findByIdOrFail(2L));
        reseller.setBanned(false);
        var savedReseller = resellerService.insert(reseller);

        Line line = new Line();
        line.setUsername("line-test");
        line.setPassword("line-test");
        line.setEmail("line@test.com");
        line.set_2FASec("secret");
        line.setNeverExpire(true);
        line.setMaxConnections(10);
        line.setOwner(savedReseller);
        line.setCountryLocked(true);
        line.setForcedCountry(CountryCode.IR);
        line.setRole(roleService.findByIdOrFail(3L));
        var savedLine = lineService.insert(line);

        //create stream
        Channel channel = new Channel();
        channel.setName("On Demand Channel");
        List<String> streamInputs = new ArrayList<>();
        streamInputs.add("http://tivix.eu:8000/iptenjoyim/3GaALqqI2tcL/62748");
        channel.setStreamInputs(streamInputs);
        var savedChannel = channelService.insert(channel, false);

        //create server
        ServerInsertView insertView = new ServerInsertView();
        insertView.setIp("127.0.0.1");
        insertView.setCorePort("8081");
        insertView.setName("Core Test");
        insertView.setInterfaceName("wlp3s0");
        var savedServerView = serverService.add(insertView);

        // connect stream to server as an on-demand channel
        StreamServer streamServer = new StreamServer(new StreamServerId(savedChannel.getId(), savedServerView.getId()));
        StreamDetails streamDetails = new StreamDetails();
        streamDetails.setStreamStatus(StreamStatus.OFFLINE);
        streamServer.setStreamDetails(streamDetails);
        List<StreamServer> streamServers = new ArrayList<>();
        streamServer.setStream(savedChannel);
        streamServer.setOnDemand(true);
        streamServer.setServer(serverMapper.convertToEntity(savedServerView));
        streamServers.add(streamServer);
        var savedServer = serverMapper.convertToEntity(savedServerView);
        savedServer.setStreamServers(streamServers);
        serverService.updateOrFail(savedServer.getId(), savedServer);

        // if the status is equal to ture, this means that the channel has to be started on the selected server.
        var status = channelService.checkOnDemandStatus(streamServer);
        Assertions.assertTrue(status);
    }

    @Test
    public void stopOnDemand(){
        //create one line
        Reseller reseller = new Reseller();
        reseller.setUsername("reseller");
        reseller.setPassword("reseller");
        reseller.setEmail("reseller@test.com");
        reseller.setCredits(1);
        reseller.setRole(roleService.findByIdOrFail(2L));
        reseller.setBanned(false);
        var savedReseller = resellerService.insert(reseller);

        Line line = new Line();
        line.setUsername("line-test");
        line.setPassword("line-test");
        line.setEmail("line@test.com");
        line.set_2FASec("secret");
        line.setNeverExpire(true);
        line.setMaxConnections(10);
        line.setOwner(savedReseller);
        line.setCountryLocked(true);
        line.setForcedCountry(CountryCode.IR);
        line.setRole(roleService.findByIdOrFail(3L));
        var savedLine = lineService.insert(line);

        //create one on-demand channel.
        Channel channel = new Channel();
        channel.setName("On Demand Channel");
        List<String> streamInputs = new ArrayList<>();
        streamInputs.add("http://tivix.eu:8000/iptenjoyim/3GaALqqI2tcL/62748");
        channel.setStreamInputs(streamInputs);
        var savedChannel = channelService.insert(channel, false);

        // create one server.
        ServerInsertView serverView = new ServerInsertView();
        serverView.setIp("127.0.0.1");
        serverView.setCorePort("8081");
        serverView.setName("Core Test");
        serverView.setInterfaceName("wlp3s0");
        var savedServerView = serverService.add(serverView);

        // connect stream to server as an on-demand channel
        StreamServer streamServer = new StreamServer(new StreamServerId(savedChannel.getId(), savedServerView.getId()));
        StreamDetails streamDetails = new StreamDetails();
        streamDetails.setStreamStatus(StreamStatus.ONLINE);
        streamServer.setStreamDetails(streamDetails);
        List<StreamServer> streamServers = new ArrayList<>();
        streamServer.setStream(savedChannel);
        streamServer.setOnDemand(true);
        streamServer.setServer(serverMapper.convertToEntity(savedServerView));
        streamServers.add(streamServer);
        var savedServer = serverMapper.convertToEntity(savedServerView);
        savedServer.setStreamServers(streamServers);
        var finalUpdatedServer = serverService.updateOrFail(savedServer.getId(), savedServer);

        //create multiple connections.
        var newConnection = new Connection(savedLine, savedChannel, finalUpdatedServer, "1.1.1.1");
        var newConnection2 = new Connection(savedLine, savedChannel, finalUpdatedServer, "1.1.1.2");
        var newConnection3 = new Connection(savedLine, savedChannel, finalUpdatedServer, "1.1.1.3");

        List<Long> streamIds = new ArrayList<>();
        channelService.checkOnDemandConnections(streamIds, finalUpdatedServer);
        Assertions.assertEquals(1l, streamIds.size());
    }
}
