package app.web.mapper;

import app.model.ActivityLog;
import app.web.dto.ActivityLogResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static ActivityLogResponse fromActivityLog(ActivityLog activityLog) {
        return ActivityLogResponse.builder()
                .userId(activityLog.getUserId())
                .action(activityLog.getAction())
                .createdOn(activityLog.getCreatedOn())
                .build();
    }
}
