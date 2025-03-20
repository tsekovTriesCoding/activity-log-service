package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ActivityLogRequest {
    private String action;
    private UUID userId;
}
