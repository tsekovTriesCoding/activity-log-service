package app.web;

import app.model.ActivityLog;
import app.repository.ActivityLogRepository;
import app.web.dto.ActivityLogRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ActivityLogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @BeforeEach
    void setUp() {
        activityLogRepository.deleteAll(); // Clean database before each test
    }

    @Test
    void testLogActivity_shouldLogActivity() throws Exception {
        ActivityLogRequest request = ActivityLogRequest.builder()
                .userId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .action("action1")
                .build();

        mockMvc.perform(post("/api/v1/activity-log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.action").isNotEmpty())
                .andExpect(jsonPath("$.createdOn").isNotEmpty());

        assertTrue(activityLogRepository.existsByUserId(request.getUserId()));
        assertEquals(1, activityLogRepository.findAllByUserIdAndIsDeletedIsFalse(request.getUserId()).size());
    }

    @Test
    void testGetActivityLog_shouldGetActivityLog() throws Exception {
        UUID userId = UUID.randomUUID();
        ActivityLog activityLog = ActivityLog.builder()
                .userId(userId)
                .action("action1")
                .createdOn(LocalDateTime.now())
                .build();
        activityLogRepository.save(activityLog);

        mockMvc.perform(get("/api/v1/activity-log")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].userId").isNotEmpty())
                .andExpect(jsonPath("$[0].action").isNotEmpty())
                .andExpect(jsonPath("$[0].createdOn").isNotEmpty());
    }

    @Test
    void testDeleteActivityLog_shouldDeleteActivityLog() throws Exception {
        UUID userId = UUID.randomUUID();
        ActivityLog activityLog = ActivityLog.builder()
                .userId(userId)
                .action("action1")
                .createdOn(LocalDateTime.now())
                .build();
        activityLogRepository.save(activityLog);

        mockMvc.perform(delete("/api/v1/activity-log")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Activity logs deleted for user: " + userId));

        mockMvc.perform(get("/api/v1/activity-log")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void testLogActivity_shouldFailWhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/activity-log")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetActivityLog_shouldReturnEmptyListWhenUserHasNoLogs() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/activity-log")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0)); // Expect empty list
    }

    @Test
    void testGetActivityLog_shouldFailWhenUserIdIsMissingForGetRequest() throws Exception {
        mockMvc.perform(get("/api/v1/activity-log"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteActivityLog_shouldFailWhenUserIdIsMissingForDeleteRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/activity-log"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteActivityLog_shouldReturnNotFoundWhenDeletingNonExistentLogs() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/activity-log")
                        .param("userId", userId.toString()))
                .andExpect(status().isNotFound());
    }
}
