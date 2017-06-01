package jittr.db;

import org.springframework.data.jpa.repository.JpaRepository;

import jittr.domain.Jittle;
import jittr.domain.Jittle.TargetQueue;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Automatic JPA repository interface with operations for {@link Jittle} persistence.
 * @author Grehov
 */
public interface JittleRepository extends JpaRepository<Jittle, Long>, JittleRepositoryCustom {
  
  List<Jittle> findByJitterId(long idJitter);
  List<Jittle> findByJitterUsername(@NotNull String jitterUsername);
  List<Jittle> findByJitterUsernameAndTQueue(@NotNull String jitterUsername, 
          @NotNull TargetQueue tQueue);  
  int deleteByJitterUsernameAndTQueue(@NotNull String jitterUsername, @NotNull TargetQueue tQueue); 
}
