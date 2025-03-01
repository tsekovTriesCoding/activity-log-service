package app.repository;

import app.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    List<ActivityLog> findAllByUserIdAndIsDeletedIsFalse(UUID userId);

    void deleteAllByUserId(UUID userId);
}
