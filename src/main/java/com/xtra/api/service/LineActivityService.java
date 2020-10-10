package com.xtra.api.service;

import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineActivityId;
import com.xtra.api.model.Stream;
import com.xtra.api.repository.LineActivityRepository;
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
        lineActivityRepository.deleteById(activityId);
    }

    //@Transactional
    public void batchCreateOrUpdate(List<LineActivity> lineActivities) {
        for (var activity : lineActivities) {
            //@todo find out how null values get passed
            var existingActivity = lineActivityRepository.findById(activity.getId());
            if (existingActivity.isEmpty()) {
                var lineById = lineService.findById(activity.getId().getLineId());
                if (lineById.isEmpty())
                    continue;
                activity.setLine(lineById.get());
                var streamById = streamService.findById(activity.getId().getStreamId());
                if (streamById.isEmpty())
                    continue;
                activity.setStream((Stream) streamById.get());
                lineActivityRepository.save(activity);

            } else {
                var oldActivity = existingActivity.get();
                copyProperties(activity, oldActivity, "id", "line", "stream");
                var streamById = streamService.findById(activity.getId().getStreamId());
                if (streamById.isEmpty())
                    continue;
                activity.setStream((Stream) streamById.get());
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

}
