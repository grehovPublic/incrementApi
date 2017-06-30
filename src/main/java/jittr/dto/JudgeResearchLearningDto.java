package jittr.dto;

import jittr.domain.JudgeResearch.State;

/**
 * DTO for implementation of {@link IJudgeResearch} in stage LEARNING.
 * 
 * @author Grehov
 *
 */
public class JudgeResearchLearningDto extends JudgeResearchDto {
    
    public JudgeResearchLearningDto(String name) {
        super(name);
        this.state = State.LEARNING;
    } 
    
    public JudgeResearchLearningDto() {
    } 
}
