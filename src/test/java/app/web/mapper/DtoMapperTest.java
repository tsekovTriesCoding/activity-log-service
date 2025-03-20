package app.web.mapper;

import app.model.ActivityLog;
import app.web.dto.ActivityLogResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperTest {

    @Test
    public void testFromActivityLog_ShouldMapTo_ActivityLogResponse() {
        ActivityLog activityLog = ActivityLog.builder()
                .userId(UUID.randomUUID())
                .action("Test Action")
                .isDeleted(false)
                .createdOn(LocalDateTime.now())
                .build();

        ActivityLogResponse activityLogResponse = DtoMapper.fromActivityLog(activityLog);

        assertEquals(activityLogResponse.getUserId(), activityLog.getUserId());
        assertEquals(activityLog.getAction(), activityLog.getAction());
        assertEquals(activityLog.getCreatedOn(), activityLog.getCreatedOn());
        assertEquals(activityLog.getCreatedOn(), activityLog.getCreatedOn());
    }
}
