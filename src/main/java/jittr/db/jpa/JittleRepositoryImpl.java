package jittr.db.jpa;

import java.util.List;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jittr.db.JittleRepositoryCustom;
import jittr.domain.Jittle;

/**
 * JPA repository class. Implements custom operations for {@link Jittle} persistence.
 * @author Grehov
 *
 */
@Repository
public class JittleRepositoryImpl implements JittleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Jittle> findRecent() {
        return findRecent(10);
    }
    
    public List<Jittle> findRecent(int count) {
        //!!!
        return (List<Jittle>) entityManager
                .createQuery("select s from Jittle s order by s.postedTime desc", Jittle.class)
                .setMaxResults(count)
                .getResultList();
    }
}
