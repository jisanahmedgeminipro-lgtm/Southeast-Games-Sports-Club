package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the centralized {@link MediaAsset} library. Basic CRUD is
 * sufficient; assets are referenced by id from owning entities.
 */
@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
}
