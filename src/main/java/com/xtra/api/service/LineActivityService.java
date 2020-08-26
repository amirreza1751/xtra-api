package com.xtra.api.service;

import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineActivityId;
import com.xtra.api.repository.LineActivityRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class LineActivityService {
    final private LineActivityRepository lineActivityRepository;
    final private LineService lineService;
    final private StreamService streamService;

    @Autowired
    public LineActivityService(LineActivityRepository lineActivityRepository, LineService lineService, StreamService streamService) {
        this.lineActivityRepository = lineActivityRepository;
        this.lineService = lineService;
        this.streamService = streamService;
    }

    public void deleteLineActivity(LineActivityId activityId) {
        lineActivityRepository.deleteByLineIdAndUserIpAndStreamId(activityId.getLineId(), activityId.getUserIp(), activityId.getStreamId());
    }

    //@Transactional
    public void batchCreateOrUpdate(List<LineActivity> lineActivities) {
        for (var activity : lineActivities) {
            //@todo find out how null values get passed
            if (activity.getLineId() == null || activity.getStreamId() == null || StringUtils.isEmpty(activity.getUserIp()))
                continue;

            var existingActivity = lineActivityRepository.findByLineIdAndUserIpAndStreamId(activity.getLineId(), activity.getUserIp(), activity.getStreamId());
            if (existingActivity.isEmpty()) {
                System.out.println("new activity");

                var lineById = lineService.findById(activity.getLineId());
                if (lineById.isEmpty())
                    continue;
                activity.setLine(lineById.get());
            } else {
                var oldActivity = existingActivity.get();
                System.out.println("old activity");
                copyProperties(oldActivity, activity, "id", "user", "stream");
            }

            var streamById = streamService.findById(activity.getStreamId());
            if (streamById.isEmpty())
                continue;
            activity.setStream(streamById.get());

            lineActivityRepository.save(activity);
        }
    }

    @Transactional
    public void batchDelete(List<LineActivity> lineActivities) {
        for (var activity : lineActivities) {
            lineActivityRepository.deleteByLineIdAndUserIpAndStreamId(activity.getLineId(), activity.getUserIp(), activity.getStreamId());
        }
    }
}
