package app.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ActivityLogRequest {
    private String action;
    private UUID userId;
}
