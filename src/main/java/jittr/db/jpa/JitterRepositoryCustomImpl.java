package jittr.db.jpa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import jittr.db.JitterRepository;
import jittr.db.JitterRepositoryCustom;
import jittr.domain.Jitter;
import jittr.domain.Jitter.Role;

/**
 * JPA repository class. Implements custom operations for {@link Jitter} persistence.
 * @author Grehov
 *
 */
@Repository
public class JitterRepositoryCustomImpl implements JitterRepositoryCustom{
    
    private final static String DEFAULT_PASSWORD = "";
    private final static Jitter.Role DEFAULT_ROLE = Role.ROLE_JITTER;
    
    @Autowired
    JitterRepository repository;

    @PersistenceContext
    private EntityManager entityManager;  

    @Override
    @Transactional
    public Jitter merge(final Jitter jitter) {
        return repository.findByUsername(jitter.getUsername())
                .map(found -> {
                    found.setFullName(jitter.getFullName());
                    found.setEmail(jitter.getEmail());
                    return repository.save(found);})
                .orElseGet(() -> repository.save(new Jitter(null, jitter.getUsername(),
                        DEFAULT_PASSWORD, jitter.getFullName(), jitter.getEmail(), DEFAULT_ROLE)));
        }
    

}
