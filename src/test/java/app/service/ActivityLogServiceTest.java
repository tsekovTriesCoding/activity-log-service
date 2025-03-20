package app.service;

import app.model.ActivityLog;
import app.repository.ActivityLogRepository;
import app.web.dto.ActivityLogRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    private UUID userId;
    private ActivityLog activityLog1;
    private ActivityLog activityLog2;
    private List<ActivityLog> activities;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        activityLog1 = ActivityLog.builder()
                .userId(userId)
                .action("action1")
                .createdOn(LocalDateTime.now())
                .build();

        activityLog2 = ActivityLog.builder()
                .userId(userId)
                .action("action2")
                .createdOn(LocalDateTime.now())
                .build();

        activities = List.of(activityLog1, activityLog2);
    }

    @Test
    public void testLogActivity_ShouldSaveActivityToDatabase() {
        ActivityLogRequest activityLogRequest = ActivityLogRequest.builder()
                .userId(userId)
                .action("action1")
                .build();

        when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(activityLog1);

        ActivityLog loggedActivity = activityLogService.logActivity(activityLogRequest);

        assertEquals(activityLogRequest.getUserId(), loggedActivity.getUserId());
        assertEquals(activityLogRequest.getAction(), loggedActivity.getAction());

        verify(activityLogRepository, times(1)).save(any());
    }

    @Test
    public void testGetByUserId_ShouldReturnActivityLog_WhenThereAreActivitiesInDatabase() {
        when(activityLogRepository.findAllByUserIdAndIsDeletedIsFalseOrderByCreatedOnDesc(any(UUID.class)))
                .thenReturn(List.of(activityLog1, activityLog2));

        List<ActivityLog> activitiesByUserId = activityLogService.getByUserId(userId);

        assertEquals(2, activitiesByUserId.size());
        assertEquals(activityLog1, activitiesByUserId.get(0));
        assertEquals(activityLog2, activitiesByUserId.get(1));

        verify(activityLogRepository, times(1)).findAllByUserIdAndIsDeletedIsFalseOrderByCreatedOnDesc(any(UUID.class));
        verify(activityLogRepository, times(0)).findAllByUserIdAndIsDeletedIsFalse(any(UUID.class));
    }

    @Test
    public void testGetByUserId_ShouldReturnEmpty_WhenThereAreNoActivitiesInDatabase() {
        when(activityLogRepository.findAllByUserIdAndIsDeletedIsFalseOrderByCreatedOnDesc(any(UUID.class)))
                .thenReturn(new ArrayList<>());

        List<ActivityLog> activitiesByUserId = activityLogService.getByUserId(userId);

        assertEquals(0, activitiesByUserId.size());

        verify(activityLogRepository, times(1)).findAllByUserIdAndIsDeletedIsFalseOrderByCreatedOnDesc(any(UUID.class));
        verify(activityLogRepository, times(0)).findAllByUserIdAndIsDeletedIsFalse(any(UUID.class));
    }

    @Test
    public void testDeleteByUserId_ShouldDeleteActivityLog_WhenThereAreActivitiesInDatabaseForUser() {
        when(activityLogRepository.findAllByUserIdAndIsDeletedIsFalse(userId))
                .thenReturn(activities);
        when(activityLogRepository.existsByUserId(userId)).thenReturn(true);

        assertEquals(2, activityLogRepository.findAllByUserIdAndIsDeletedIsFalse(userId).size());

        boolean isDeleted = activityLogService.deleteByUserId(userId);

        assertTrue(isDeleted, "Deletion should return true");

        for (ActivityLog log : activities) {
            assertTrue(log.isDeleted(), "Activity log should be marked as deleted");
        }

        verify(activityLogRepository, times(1)).existsByUserId(userId);
        verify(activityLogRepository, times(2)).findAllByUserIdAndIsDeletedIsFalse(userId);
        verify(activityLogRepository, times(activities.size())).save(any(ActivityLog.class));
    }

    @Test
    public void testDeleteByUserId_ShouldNotDeleteActivityLog_WhenThereNotActivitiesInDatabaseFoUser() {
        when(activityLogRepository.existsByUserId(userId)).thenReturn(false);

        boolean isDeleted = activityLogService.deleteByUserId(userId);

        assertFalse(isDeleted, "Deletion should return false");

        verify(activityLogRepository, times(1)).existsByUserId(userId);
        verify(activityLogRepository, times(0)).findAllByUserIdAndIsDeletedIsFalse(userId);
        verify(activityLogRepository, times(0)).save(any(ActivityLog.class));
    }
}
