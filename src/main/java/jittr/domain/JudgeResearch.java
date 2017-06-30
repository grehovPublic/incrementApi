package jittr.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static jittr.domain.SharedConstants.*;

/**
 * Abstract class for all domain entities, implementations of {@link IJudgeResearch}.
 * 
 * @author Grehov
 *
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "JR_TYPE")
public abstract class JudgeResearch {
    
    public static enum State {BRANDNAME, LEARNING, TREE, PAYMENT, PROCESSING, READY};
    
    protected static final String NAME_SIZE = "{research-name.size}";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    protected State state;
    
    @Column(nullable = false)
    protected String name;
    
    @ManyToOne
    @JoinColumn(name = COLNAME_JITTER)
    private Jitter jitter;

    /**
     * Default constructor for JPA.
     */
    public JudgeResearch () {
    }
    
    public JudgeResearch (final String name) {
        this.name = name;
        this.state = State.BRANDNAME;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
  
    public void setName(final String name) {
        this.name = name;
    }
    
    public State getState() {
        return this.state;
    }
    
    public void setState(final State state) {
        this.state = state;
    }
    
    public Jitter getJitter() {
        return jitter;
    }

    public void setJitter(Jitter jitter) {
        this.jitter = jitter;
    }
    
    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that, ID_FIELD);
    }
    
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, ID_FIELD);
    }
    
    @Override
    public String toString() {
        return id.toString() + " " + name.toString();
    }
    
}
