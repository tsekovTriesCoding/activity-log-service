package app.service;

import app.model.ActivityLog;
import app.repository.ActivityLogRepository;
import app.web.dto.ActivityLogRequest;
import app.web.dto.ActivityLogResponse;
import app.web.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLog logActivity(ActivityLogRequest request) {
        ActivityLog log = ActivityLog.builder()
                .userId(request.getUserId())
                .action(request.getAction())
                .createdOn(LocalDateTime.now())
                .build();

        return activityLogRepository.save(log);
    }

    public List<ActivityLogResponse> getByUserId(UUID userId) {
        List<ActivityLog> logs = activityLogRepository.findAllByUserIdAndIsDeletedIsFalse(userId);

        return logs.stream().map(DtoMapper::fromActivityLog).collect(Collectors.toList());
    }

    public boolean deleteByUserId(UUID userId) {
        if (activityLogRepository.existsByUserId(userId)) {
            activityLogRepository.findAllByUserIdAndIsDeletedIsFalse(userId)
                    .forEach(activityLog -> {
                activityLog.setDeleted(true);
                activityLogRepository.save(activityLog);
            });

            return true;
        }
        return false;
    }
}