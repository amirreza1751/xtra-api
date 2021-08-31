package com.xtra.api.unit.service.admin;

import com.xtra.api.mapper.admin.ChannelMapper;
import com.xtra.api.mapper.admin.ChannelStartMapper;
import com.xtra.api.model.stream.Channel;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.EpgChannelRepository;
import com.xtra.api.repository.StreamInputRepository;
import com.xtra.api.service.admin.ChannelService;
import com.xtra.api.service.admin.LoadBalancingService;
import com.xtra.api.service.admin.ServerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ChannelRepository.class, ChannelStartMapper.class, ChannelService.class,
        ChannelMapper.class, LoadBalancingService.class, ServerService.class, EpgChannelRepository.class,
        StreamInputRepository.class})
@ExtendWith(SpringExtension.class)
class ChannelServiceTest {

    @MockBean
    private ChannelMapper channelMapper;

    @MockBean
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelService channelService;

    @MockBean
    private ChannelStartMapper channelStartMapper;

    @MockBean
    private EpgChannelRepository epgChannelRepository;

    @MockBean
    private LoadBalancingService loadBalancingService;

    @MockBean
    private ServerService serverService;

    @MockBean
    private StreamInputRepository streamInputRepository;

    @Test
    public void testUpdate() {
        when(this.channelRepository.findById(any())).thenThrow(new RuntimeException("An error occurred"));
        this.channelService.update(123L, new Channel(), true);
        verify(this.channelRepository).findById(any());
    }

}