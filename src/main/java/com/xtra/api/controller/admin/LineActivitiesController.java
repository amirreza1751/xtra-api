package com.xtra.api.controller.admin;

import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineActivityId;
import com.xtra.api.projection.admin.ConnectionIdView;
import com.xtra.api.projection.admin.ConnectionView;
import com.xtra.api.service.admin.LineActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/line_activities")
public class LineActivitiesController {
    final private LineActivityService lineActivityService;

    @Autowired
    public LineActivitiesController(LineActivityService lineActivityService) {
        this.lineActivityService = lineActivityService;
    }

    @PostMapping("/batch")
    public ResponseEntity<?> batchCreateOrUpdateActivities(@RequestBody List<LineActivity> lineActivities, @RequestParam String portNumber, HttpServletRequest request) {
        lineActivityService.batchCreateOrUpdate(lineActivities, portNumber, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteActivities(@RequestBody List<LineActivity> lineActivities) {
        lineActivityService.batchDelete(lineActivities);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteLineActivity(@RequestBody LineActivityId activityId) {
        lineActivityService.deleteLineActivity(activityId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping()
    public ResponseEntity<Page<ConnectionView>> activeConnections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                  @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir){
        return ResponseEntity.ok(lineActivityService.activeConnections(pageNo, pageSize, sortBy, sortDir));
    }

    @PostMapping("/end")
    public ResponseEntity endConnection(ConnectionIdView connectionIdView){
        lineActivityService.endConnection(connectionIdView);
        return ResponseEntity.ok().build();
    }

}
