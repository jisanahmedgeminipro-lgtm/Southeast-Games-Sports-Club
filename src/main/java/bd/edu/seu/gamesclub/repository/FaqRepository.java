package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.Faq;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Faq} entries.
 */
@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

    /** Active FAQs ordered by display order. */
    List<Faq> findByActiveTrueOrderByDisplayOrderAsc();
}
