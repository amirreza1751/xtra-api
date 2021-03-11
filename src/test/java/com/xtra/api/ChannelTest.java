package com.xtra.api;

import static org.assertj.core.api.Assertions.assertThat;
import com.xtra.api.controller.admin.ChannelController;
import com.xtra.api.model.Channel;
import com.xtra.api.repository.ChannelRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class ChannelTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChannelController controller;

    @Autowired
    ChannelRepository channelRepository;

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
        channel.put("name", "amirakk22");
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
                .andExpect(status().is2xxSuccessful())
        ;
        assertThat(channelRepository.findById(id).isEmpty());

    }
}
