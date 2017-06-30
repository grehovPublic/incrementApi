package jittr.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Domain entity, implementations of {@link IJudgeResearch} in stage BRAND.
 * 
 * @author Grehov
 *
 */
@Entity
@DiscriminatorValue("BRAND")
public class JudgeResearchBrand extends JudgeResearch {

    public JudgeResearchBrand(String name) {
        super(name);
    }
    
    public JudgeResearchBrand() {
    }
}
