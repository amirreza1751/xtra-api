package com.xtra.api;

import com.xtra.api.model.Channel;
import com.xtra.api.repository.ChannelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("dev")
@DataJpaTest
public class ChannelOperations {

    @Autowired
    ChannelRepository channelRepository;

    @Test
    public void insertChannel() {
        Channel channel = new Channel();
        channel.setName("Test Channel");
        var savedChannel = channelRepository.save(channel);
        Long id = savedChannel.getId();
        savedChannel.setCustomFFMPEG("");
        channelRepository.save(savedChannel);
        assertThat(channelRepository.findById(id).isPresent());
    }
}
