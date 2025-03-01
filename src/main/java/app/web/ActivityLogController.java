package app.web;

import app.model.ActivityLog;
import app.service.ActivityLogService;
import app.web.dto.ActivityLogRequest;
import app.web.dto.ActivityLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.web.mapper.DtoMapper.fromActivityLog;

@RestController
@RequestMapping("/api/v1/activity-log")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @PostMapping
    public ResponseEntity<ActivityLogResponse> logActivity(@RequestBody ActivityLogRequest request) {
        ActivityLog activityLog = activityLogService.logActivity(request);

        ActivityLogResponse activityLogResponse = fromActivityLog(activityLog);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(activityLogResponse);
    }

    
}
