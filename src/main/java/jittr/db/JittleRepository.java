package jittr.db;

import jittr.domain.Jittle;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Automatic JPA repository interface with operations for {@link Jittle} persistence.
 * @author Grehov
 */
public interface JittleRepository extends JpaRepository<Jittle, Long> {
  
  List<Jittle> findByJitterId(long idJitter);
  
  List<Jittle> findByJitterUsername(String jitterUsername);
  
  Optional<Integer> deleteByJitterUsername(String jitterUsername); 
}
