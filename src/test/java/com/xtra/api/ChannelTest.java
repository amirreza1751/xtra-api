package com.xtra.api;

import static org.assertj.core.api.Assertions.assertThat;
import com.xtra.api.controller.admin.ChannelController;
import com.xtra.api.model.Channel;
import com.xtra.api.repository.ChannelRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
        String input = "{\"name\":\"amirak\",\"logo\":\"http://tes.com\",\"notes\":\"\",\"days_to_restart\":[\"MONDAY\"],\"time_to_restart\":\"02:00\",\"custom_ffmpeg\":\"\",\"stream_inputs\":[\"http://tes.com\"],\"stream_type\":\"CHANNEL\",\"stream_url\":\"\",\"servers\":[],\"collections\":[]}";

        this.mockMvc.perform(post("/channels").content(input).contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
        ;
    }

    @Test
    public void updateChannel() throws Exception {
        Channel channel = new Channel();
        channel.setName("Test Channel");
        var savedChannel = channelRepository.save(channel);
        Long id = savedChannel.getId();

        String input = "{\"name\":\"amirak\",\"logo\":\"http://tes.com\",\"notes\":\"\",\"days_to_restart\":[\"MONDAY\"],\"time_to_restart\":\"02:00\",\"custom_ffmpeg\":\"\",\"stream_inputs\":[\"http://tes.com\"],\"stream_type\":\"CHANNEL\",\"stream_url\":\"\",\"servers\":[],\"collections\":[]}";

        this.mockMvc.perform(patch("/channels/" + id).content(input).contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(containsString("amirak")))
        ;
    }

    @Test
    public void deleteChannel() throws Exception {
        Channel channel = new Channel();
        channel.setName("Test Channel");
        var savedChannel = channelRepository.save(channel);
        Long id = savedChannel.getId();

        this.mockMvc.perform(delete("/channels/" + id).contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(containsString("amirak")))
        ;
        assertThat(channelRepository.findById(id).isEmpty());

    }
}
