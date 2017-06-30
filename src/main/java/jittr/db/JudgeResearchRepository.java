package jittr.db;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import jittr.domain.JudgeResearch;
import jittr.domain.JudgeResearchBrand;


/**
 * Automatic JPA repository interface with operations for {@link JudgeResearchBrand} persistence.
 * 
 * @author Grehov
 */
public interface JudgeResearchRepository extends JpaRepository<JudgeResearch, Long> {
    Optional<List<JudgeResearch>> findByJitterUsernameIn(List<String>  jitterUsernames);
    Optional<List<JudgeResearch>> findByJitterUsername(String  jitterUsernames);
}

