package app.web;

import app.model.ActivityLog;
import app.service.ActivityLogService;
import app.web.dto.ActivityLogRequest;
import app.web.dto.ActivityLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public ResponseEntity<List<ActivityLogResponse>> getActivityLog(@RequestParam(name = "userId") UUID userId) {
        List<ActivityLogResponse> activityLog = activityLogService.getByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(activityLog);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteActivityLog(@RequestParam(name = "userId") UUID userId) {
        boolean deleted = activityLogService.deleteByUserId(userId);

        if (deleted) {
            return ResponseEntity.ok("Activity logs deleted for user: " + userId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}