package jittr.db;

import org.springframework.data.jpa.repository.JpaRepository;

import jittr.domain.Jitter;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * Automatic JPA repository interface with operations for {@link Jitter} persistence.
 * @author Grehov
 */
public interface JitterRepository extends JpaRepository<Jitter, Long> {
    
    Optional<Jitter> findByUsername(@NotNull String username);
    
    List<Jitter> findByUsernameOrFullNameLike(@NotNull String username, @NotNull String fullName);
}
