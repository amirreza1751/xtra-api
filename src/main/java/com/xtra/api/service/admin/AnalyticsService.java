package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.MovieMapper;
import com.xtra.api.mapper.admin.SeriesMapper;
import com.xtra.api.model.line.Line;
import com.xtra.api.model.role.Role;
import com.xtra.api.model.stream.StreamStatus;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.model.user.User;
import com.xtra.api.model.user.UserType;
import com.xtra.api.projection.admin.analytics.AnalyticsData;
import com.xtra.api.projection.admin.analytics.LineAnalytics;
import com.xtra.api.projection.admin.analytics.ResellerAnalytics;
import com.xtra.api.projection.admin.analytics.ResellerExpiringLines;
import com.xtra.api.repository.*;
import com.xtra.api.service.system.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    final private ResellerRepository resellerRepository;
    final private LineRepository lineRepository;
    final private StreamServerRepository streamServerRepository;
    final private ResourceRepository resourceRepository;
    final private ServerRepository serverRepository;
    final private MovieRepository movieRepository;
    final private ConnectionRepository connectionRepository;
    final private VodConnectionRepository vodConnectionRepository;
    final private MovieMapper movieMapper;
    final private SeriesMapper seriesMapper;
    final private SeriesRepository seriesRepository;

    @Autowired
    public AnalyticsService(ResellerRepository resellerRepository, ConnectionRepository connectionRepository, LineRepository lineRepository, ChannelRepository channelRepository, ServerService serverService, StreamServerRepository streamServerRepository, ResourceRepository resourceRepository, ServerRepository serverRepository, MovieRepository movieRepository, ConnectionRepository connectionRepository1, VodConnectionRepository vodConnectionRepository, MovieMapper movieMapper, SeriesMapper seriesMapper, SeriesRepository seriesRepository) {
        this.resellerRepository = resellerRepository;
        this.lineRepository = lineRepository;
        this.streamServerRepository = streamServerRepository;
        this.resourceRepository = resourceRepository;
        this.serverRepository = serverRepository;
        this.movieRepository = movieRepository;
        this.connectionRepository = connectionRepository1;
        this.vodConnectionRepository = vodConnectionRepository;
        this.movieMapper = movieMapper;
        this.seriesMapper = seriesMapper;
        this.seriesRepository = seriesRepository;
    }

    public Object getData() {
        User currentUser = UserAuthService.getCurrentUser();
        if (currentUser.getUserType().equals(UserType.ADMIN) || currentUser.getUserType().equals(UserType.SUPER_ADMIN)){
            AnalyticsData data = new AnalyticsData();
            data.setResellerCount(resellerRepository.count());
            data.setPendingResellerCount(resellerRepository.countByIsVerifiedFalse());
            data.setOnlineUsersCount(lineRepository.countOnlineUsers().getOnlineUsersCount());
            data.setConnectionsCount(lineRepository.countOnlineUsers().getConnectionsCount());
            data.setOnlineChannelsCount(streamServerRepository.countByStreamDetails_StreamStatusIs(StreamStatus.ONLINE));
            data.setOfflineChannelsCount(streamServerRepository.countByStreamDetails_StreamStatusIs(StreamStatus.OFFLINE));
            data.setTotalInput(resourceRepository.networksBytesSum().getNetworkBytesRecv());
            data.setTotalOutput(resourceRepository.networksBytesSum().getNetworkBytesSent());
            data.setServerSummaryList(serverRepository.getServerSummaryList());
            return data;
        } else if (currentUser.getUserType().equals(UserType.RESELLER)){
            Reseller reseller = UserAuthService.getCurrentReseller();
            LocalDateTime dateTime = LocalDateTime.now();
            LocalDateTime nextWeek = dateTime.with(TemporalAdjusters.next(LocalDateTime.now().getDayOfWeek()));
            ResellerAnalytics resellerData = new ResellerAnalytics();
            resellerData.setRecentMovies(movieRepository.findTop10ByOrderByCreatedDateDesc().stream().map(movieMapper::convertToView).collect(Collectors.toList()));
            resellerData.setRecentSeries(seriesRepository.findTop10ByOrderByCreatedDateDesc().stream().map(seriesMapper::convertToView).collect(Collectors.toList()));
            resellerData.setSubResellersCount(resellerRepository.countByOwner(reseller));
            resellerData.setOnlineUsersCount(connectionRepository.countDistinctByLine_OwnerIs(reseller) + vodConnectionRepository.countDistinctByLine_OwnerIs(reseller));
            resellerData.setLinesCount(lineRepository.countAllByOwner(reseller));
            resellerData.setExpiringLines(lineRepository.findAllByExpireDateLessThanEqualAndOwnerIs(nextWeek, reseller).stream().map(expiringLines -> new ResellerExpiringLines(expiringLines.getUsername(), expiringLines.getExpireDate().toString())).collect(Collectors.toList()));
            return resellerData;
        } else {
            Line line = UserAuthService.getCurrentLine();
            LineAnalytics lineData = new LineAnalytics();
            lineData.setRecentMovies(movieRepository.findTop10ByOrderByCreatedDateDesc().stream().map(movieMapper::convertToView).collect(Collectors.toList()));
            lineData.setRecentSeries(seriesRepository.findTop10ByOrderByCreatedDateDesc().stream().map(seriesMapper::convertToView).collect(Collectors.toList()));
            lineData.setExpireDate((line.getExpireDate() != null) ? line.getExpireDate().toString() : "never");
            return lineData;
        }

    }
}
