package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the singleton {@link SystemSettings} row (id {@code 1L}).
 */
@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
}
