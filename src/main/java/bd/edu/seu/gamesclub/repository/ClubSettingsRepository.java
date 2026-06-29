package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.ClubSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the singleton {@link ClubSettings} row. The single record is
 * read/saved using id {@code 1L} via the inherited {@code findById}/{@code save}.
 */
@Repository
public interface ClubSettingsRepository extends JpaRepository<ClubSettings, Long> {
}
