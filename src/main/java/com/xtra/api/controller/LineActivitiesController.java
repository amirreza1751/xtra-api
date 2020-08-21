package com.xtra.api.controller;

import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineActivityId;
import com.xtra.api.service.LineActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/line_activities")
public class LineActivitiesController {
    final private LineActivityService lineActivityService;

    @Autowired
    public LineActivitiesController(LineActivityService lineActivityService) {
        this.lineActivityService = lineActivityService;
    }

    @PutMapping("line_activities")
    public ResponseEntity<?> updateLineActivities(@RequestBody List<LineActivity> lineActivities) {
        lineActivityService.updateLineActivities(lineActivities);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("line_activities/{activityId}")
    public ResponseEntity<?> deleteLineActivity(@PathVariable LineActivityId activityId) {
        lineActivityService.deleteLineActivity(activityId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
