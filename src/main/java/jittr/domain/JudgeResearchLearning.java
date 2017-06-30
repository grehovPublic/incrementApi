package jittr.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Domain entity, implementations of {@link IJudgeResearch} in stage LEARNING.
 * 
 * @author Grehov
 *
 */

@Entity
@DiscriminatorValue("LEARNING")
public class JudgeResearchLearning extends JudgeResearch {
    
    public JudgeResearchLearning(String name) {
        super(name);
        this.state = State.LEARNING;
    }
    
    public JudgeResearchLearning() {
    }
}
