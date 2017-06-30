package jittr.db;

import jittr.domain.Jitter;

/**
 * JPA repository interface with custom operations for {@link Jitter} persistence.
 * @author Grehov
 *
 */
public interface JitterRepositoryCustom {

    /**
     * Merges {@link Jitter} with repository. Creates new, if Jitter with given username
     * is not found.
     * 
     * @param username {@link Jitter} to be merged.
     * @return mergeded {@link Jitter}.
     * 
     * @throws IllegalArgumentException if argument is null.
     */
    public Jitter merge(final Jitter jitter);
}
