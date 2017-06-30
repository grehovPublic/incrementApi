package jittr.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jittr.domain.JudgeResearch.State;


/**
 * Abstract class for all DTO implementations of {@link IJudgeResearch}.
 * 
 * @author Grehov
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = JudgeResearchBrandDto.class, name = "jittr.dto.JudgeResearchBrandDto"),
    @JsonSubTypes.Type(value = JudgeResearchLearningDto.class, name = "jittr.dto.JudgeResearchLearningDto")
})
public abstract class JudgeResearchDto implements Serializable {
      
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected static final int VALIDATION_NAME_SIZE_MIN = 3;
    protected static final int VALIDATION_NAME_SIZE_MAX = 32;
    protected static final String VALIDATION_NAME_SIZE_MESSAGE = "{research-name.size}";
    
    private Long id;

    @NotNull
    protected State state;
    
    @NotNull
    @Size(min = VALIDATION_NAME_SIZE_MIN, max = VALIDATION_NAME_SIZE_MAX,
        message = VALIDATION_NAME_SIZE_MESSAGE)
    protected String name;
    
    @NotNull
    private JitterDto jitter;

    public JudgeResearchDto () {
    }
    
    public JudgeResearchDto (final String name) {
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
    
    public JitterDto getJitter() {
        return jitter;
    }

    public void setJitter(JitterDto jitter) {
        this.jitter = jitter;
    }

}
