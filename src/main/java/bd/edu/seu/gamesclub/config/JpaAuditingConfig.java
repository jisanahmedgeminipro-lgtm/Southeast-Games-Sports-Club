package bd.edu.seu.gamesclub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA auditing, wiring the {@link AuditorAwareImpl} so the
 * {@code @CreatedBy}/{@code @LastModifiedBy} fields on {@code Auditable} are set
 * automatically. ({@code @CreatedDate}/{@code @LastModifiedDate} are handled by
 * Hibernate timestamps.)
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {
}
