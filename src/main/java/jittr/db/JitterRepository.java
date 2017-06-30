package jittr.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import jittr.domain.Jitter;

/**
 * Automatic JPA repository interface with operations for {@link Jitter} persistence.
 * @author Grehov
 */
public interface JitterRepository extends JpaRepository<Jitter, Long> {
    Optional<Jitter> findByUsername(String jitterUsername);
}
