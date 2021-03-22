package com.xtra.api.service.admin;

import com.maxmind.geoip2.model.CityResponse;
import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineActivityId;
import com.xtra.api.model.Stream;
import com.xtra.api.repository.LineActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class LineActivityService {
    final private LineActivityRepository lineActivityRepository;
    final private AdminLineServiceImpl adminLineService;
    final private StreamService streamService;
    private final ServerService serverService;
    private final GeoIpService geoIpService;

    @Autowired
    public LineActivityService(LineActivityRepository lineActivityRepository, AdminLineServiceImpl adminLineService, StreamService streamService, ServerService serverService, GeoIpService geoIpService) {
        this.lineActivityRepository = lineActivityRepository;
        this.adminLineService = adminLineService;
        this.streamService = streamService;
        this.serverService = serverService;
        this.geoIpService = geoIpService;
    }

    public void deleteLineActivity(LineActivityId activityId) {
        lineActivityRepository.deleteById(activityId);
    }

//    @Transactional
    public void batchCreateOrUpdate(List<LineActivity> lineActivities, String portNumber, HttpServletRequest request) {
        for (var activity : lineActivities) {
            //@todo find out how null values get passed
            var existingActivity = lineActivityRepository.findById(new LineActivityId(activity.getId().getLineId(), activity.getId().getStreamId(), serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber).get().getId(), activity.getId().getUserIp()));
            if (existingActivity.isEmpty()) {
                var lineById = adminLineService.findById(activity.getId().getLineId());
                if (lineById.isEmpty())
                    continue;
                activity.setLine(lineById.get());
                var streamById = streamService.findById(activity.getId().getStreamId());
                if (streamById.isEmpty())
                    continue;
                activity.setStream((Stream) streamById.get());

                var serverById = serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber);
                if (serverById.isEmpty())
                    continue;
                activity.setServer(serverById.get());
                activity = this.setIpInformation(activity.getId().getUserIp(), activity);
                lineActivityRepository.save(activity);

            } else {
                var oldActivity = existingActivity.get();
                copyProperties(activity, oldActivity, "id", "line", "stream", "server");
                var streamById = streamService.findById(activity.getId().getStreamId());
                if (streamById.isEmpty())
                    continue;
                oldActivity.setStream((Stream) streamById.get());

                var lineById = adminLineService.findById(activity.getId().getLineId());
                if (lineById.isEmpty())
                    continue;
                oldActivity.setLine(lineById.get());

                var serverById = serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber);
                if (serverById.isEmpty())
                    continue;
                oldActivity.setServer(serverById.get());
                oldActivity = this.setIpInformation(activity.getId().getUserIp(), oldActivity);
                lineActivityRepository.save(oldActivity);

            }

        }
    }

    @Transactional
    public void batchDelete(List<LineActivity> lineActivities) {
        for (var activity : lineActivities) {
            lineActivityRepository.deleteById(activity.getId());
        }
    }

    public LineActivity setIpInformation(String ip, LineActivity activity){
        Optional<CityResponse> cityResponse = geoIpService.getIpInformation(ip);
        try {
            activity.setCountry(cityResponse.map(result -> result.getCountry().getName()).orElse("Unknown Country"));
            activity.setCity(cityResponse.map(result -> result.getCity().getName()).orElse("Unknown City"));
            activity.setIsoCode(cityResponse.map(result -> result.getCountry().getIsoCode()).orElse("Unknown ISO Code"));
            activity.setIsp(cityResponse.map(result -> result.getTraits().getIsp()).orElse("Unknown ISP"));
        } catch (NullPointerException ignored){}
        return activity;
    }

}
