package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.User;
import bd.edu.seu.gamesclub.entity.enums.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link User} authentication accounts.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Look up an account by its (unique) login email. */
    Optional<User> findByEmail(String email);

    /** Whether an account already exists for the given email (registration guard). */
    boolean existsByEmail(String email);

    /** All users with the given role (e.g. every student for an "all students" broadcast). */
    List<User> findByRole(Role role);

    /** Count of users with the given role (dashboard: total students / admins). */
    long countByRole(Role role);
}
