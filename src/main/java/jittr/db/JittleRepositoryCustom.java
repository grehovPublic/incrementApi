package jittr.db;

import java.util.List;

import jittr.domain.Jittle;

/**
 * JPA repository interface with custom operations for {@link Jittle} persistence.
 * @author Grehov
 *
 */
public interface JittleRepositoryCustom {

    List<Jittle> findRecent();

    List<Jittle> findRecent(int count);
}
