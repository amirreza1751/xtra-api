package com.xtra.api.service;

import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations="classpath:application-test.properties")
@DataJpaTest
class ServerServiceTest {

//    @Autowired
//    private TestEntityManager entityManager;

    @MockBean
    private ServerRepository serverRepository;
    @MockBean
    private ChannelRepository channelRepository;


    @MockBean
    private ServerService serverService;

    @BeforeEach
    void setUp() {

    }

//    @Test
//    public void createChannel(){
//        Channel channel = new Channel();
//        channel.setName("Test Channel");
//        var savedChannel = entityManager.persist(channel);
//        Channel result = channelRepository.findByName("Test Channel").get();
//        assertThat(result.getName()).isEqualTo("Test Channel");
//    }


    @Test
    void sendStartRequest() {
        Server server = new Server();
        server.setName("core-amir");
        server.setIp("127.0.0.1");
        server.setCorePort("8081");
        serverRepository.save(server);
        Server server1 = serverRepository.findByName("core-amir").get();
//        assertThat(result.getName()).isEqualTo("core-amir");


        Channel channel = new Channel();
        channel.setName("Test Channel");
        channelRepository.save(channel);
        Channel channel1 = channelRepository.findByName("Test Channel").get();
        assertThat(channel1.getName()).isEqualTo("Test Channel");

        serverService.sendStartRequest(channel1.getId(), server1);

    }
}