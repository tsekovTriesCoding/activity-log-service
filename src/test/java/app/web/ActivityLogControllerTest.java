package app.web;

import app.model.ActivityLog;
import app.service.ActivityLogService;
import app.web.dto.ActivityLogRequest;
import app.web.dto.ActivityLogResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ActivityLogController.class)
public class ActivityLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityLogService activityLogService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private ActivityLogRequest request;
    private ActivityLog activityLog;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        LocalDateTime createdOn = LocalDateTime.now();

        request = ActivityLogRequest.builder()
                .userId(userId)
                .action("Test activity log")
                .build();

        activityLog = ActivityLog.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .action("Test activity log")
                .createdOn(createdOn)
                .build();
    }

    @Test
    public void testLogActivity_ShouldReturnCreated() throws Exception {
        when(activityLogService.logActivity(any(ActivityLogRequest.class))).thenReturn(activityLog);

        mockMvc.perform(post("/api/v1/activity-log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.action").value(activityLog.getAction()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);
                    LocalDateTime actualCreatedOn = LocalDateTime.parse(jsonNode.get("createdOn").asText());

                    assertEquals(activityLog.getCreatedOn().truncatedTo(ChronoUnit.MICROS),
                            actualCreatedOn.truncatedTo(ChronoUnit.MICROS));
                }); // Java serializes and deserializes LocalDateTime differently:
        // The actual output trims unnecessary trailing zeros in the nanosecond field.

        verify(activityLogService, times(1)).logActivity(any(ActivityLogRequest.class));
    }

    @Test
    public void testGetActivityLog_ShouldReturnListOfLogs() throws Exception {
        when(activityLogService.getByUserId(userId)).thenReturn(List.of(activityLog));

        mockMvc.perform(get("/api/v1/activity-log")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].action").value(activityLog.getAction()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse).get(0);
                    LocalDateTime actualCreatedOn = LocalDateTime.parse(jsonNode.get("createdOn").asText());

                    assertEquals(activityLog.getCreatedOn().truncatedTo(ChronoUnit.MICROS),
                            actualCreatedOn.truncatedTo(ChronoUnit.MICROS));
                });

        verify(activityLogService, times(1)).getByUserId(userId);
    }

    @Test
    public void testDeleteActivityLog_ShouldReturnOk() throws Exception {
        when(activityLogService.deleteByUserId(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/activity-log")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Activity logs deleted for user: " + userId));

        verify(activityLogService, times(1)).deleteByUserId(userId);
    }

    @Test
    public void testDeleteActivityLog_ShouldReturnNotFound() throws Exception {
        when(activityLogService.deleteByUserId(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/activity-log")
                        .param("userId", userId.toString()))
                .andExpect(status().isNotFound());

        verify(activityLogService, times(1)).deleteByUserId(userId);
    }
}
