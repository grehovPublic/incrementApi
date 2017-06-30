package jittr.dto;

import static jittr.domain.SharedConstants.*;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Range;

import jittr.domain.Jitter;
import jittr.domain.Jittle;
import jittr.domain.Jittle.Judgment;
import jittr.domain.Jittle.TargetQueue;

/**
 * The DTO for {@link Jittle}. 
 */
public class JittleDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected static final int MESSAGE_MIN_LENGTH = 0;
    protected static final int MESSAGE_MAX_LENGTH = 140;
    protected static final String VALIDATE_NOTE_MESSAGE_SIZE = "{message.size}";
    
    protected static final int COUNTRY_MIN_LENGTH = 3;
    protected static final int COUNTRY_MAX_LENGTH = 32;
    protected static final String VALIDATE_NOTE_COUNTRY_SIZE = "{country.size}";
    
    protected static final long LATITUDE_MIN = -90;
    protected static final long LATITUDE_MAX =  90;
    protected static final String VALIDATE_NOTE_LATITUDE = "{latitude.size}";
    
    protected static final long LONGITUDE_MIN = -180;
    protected static final long LONGITUDE_MAX =  180;
    protected static final String VALIDATE_NOTE_LONGITUDE = "{longitude.size}";

    @NotNull
    private Long id;

    @NotNull
    private JitterDto jitter;

    @NotNull
    @Size(min = MESSAGE_MIN_LENGTH, max = MESSAGE_MAX_LENGTH, message = VALIDATE_NOTE_MESSAGE_SIZE)
    private String message;

    @NotNull
    private Date postedTime;
    
    @NotNull
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, 
          message = VALIDATE_NOTE_USERNAME_SIZE)
    @Pattern(regexp = USERNAME_PATTERN, message = VALIDATE_NOTE_USERNAME_PATTERN)
    private String author;

    @NotNull
    private Judgment judgment;
    
    @NotNull
    private TargetQueue tQueue;

    @NotNull
    @Size(min = COUNTRY_MIN_LENGTH, max = COUNTRY_MAX_LENGTH, message = VALIDATE_NOTE_COUNTRY_SIZE)
    private String country;

    @Range(min = LATITUDE_MIN, max = LATITUDE_MAX, message = VALIDATE_NOTE_LATITUDE)
    private Float latitude;

    @Range(min = LONGITUDE_MIN, max = LONGITUDE_MAX, message = VALIDATE_NOTE_LONGITUDE)
    private Float longitude;
    
    /**
     * {@link Jittle#Jittle()}
     */
    public JittleDto() {
        this.setJudgment(Judgment.NONE);
        this.latitude = Float.NaN;
        this.longitude = Float.NaN;
        this.country = "";
    }

    /**
     * {@link Jittle#Jittle(Long, Jitter, String, Date, String, jittr.domain.Jittle.TargetQueue, String)}
     */
    public JittleDto(final Long id, final JitterDto jitter, final String message, 
                  final Date postedTime, final String author, 
                  final TargetQueue tQueue, final String country) {
        this();
        this.id = id;
        this.jitter = jitter;
        this.message = message;
        this.postedTime = postedTime;
        this.author = author;
        this.tQueue = tQueue;
        this.country = country;
    }

    /**
     * {@link Jittle#getId()}
     */
    public Long getId() { return this.id; }
    
    /**
     * {@link Jittle#setId(Long)}
     */
    public void setId(final Long id) { this.id = id; }

    /**
     * {@link Jittle#getMessage()}
     */
    public String getMessage() { return this.message; }
    
    /**
     * {@link Jittle#setMessage(String)}
     */
    public void setMessage(final String message) { this.message = message; }

    /**
     * {@link Jittle#getPostedTime()}
     */
    public Date getPostedTime() { return this.postedTime; }
    
    /**
     * {@link Jittle#setPostedTime(Date)}
     */
    public void setPostedTime(final Date postedTime) { this.postedTime = postedTime; }
    
    /**
     * {@link Jittle#getAuthor()}
     */
    public String getAuthor() { return this.author; }
 
    /**
     * {@link Jittle#setAuthor(String)}
     */
    public void setAuthor(final String author) { this.author = author; }

    /**
     * {@link Jittle#getJitter()}
     */
    public JitterDto getJitter() { return this.jitter; }
    

    /**
     * {@link Jittle#setJitter(Jitter)}
     */
    public void setJitter(final JitterDto jitter) { this.jitter  = jitter; }
   

    /**
     * {@link Jittle#getJudgment()}
     */
    public Judgment getJudgment() { return this.judgment; }

    /**
     * {@link Jittle#setJudgment(jittr.domain.Jittle.Judgment)}
     */
    public void setJudgment(final Judgment judgment) {
        this.judgment = judgment;
    }
    
    /**
     * {@link Jittle#getTQueue()}
     */
    public TargetQueue getTQueue() { return this.tQueue; }

    /**
     * {@link Jittle#setTQueue(jittr.domain.Jittle.TargetQueue)}         
     */
    public void setTQueue(final TargetQueue tQueue) {
        this.tQueue = tQueue;
    }

    /**
     * {@link Jittle#getCountry()}
     */
    public String getCountry() { return this.country; }
   
    /**
     * {@link Jittle#setCountry(String)}
     */
    public void setCountry(final String country) { this.country = country; }

    /**
     * {@link Jittle#getLatitude()}
     */
    public Float getLatitude() { return this.latitude; }
    
    /**
     * {@link Jittle#setLatitude(Float)}
     */
    public void setLatitude(final Float latitude) { this.latitude = latitude; }

    /**
     * {@link Jittle#getLongitude()}
     */
    public Float getLongitude() { return this.longitude; }
    
    /**
     * {@link Jittle#setLongitude(Float)}
     */
    public void setLongitude(final Float longitude) { this.longitude = longitude; }
    
    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that, ID_FIELD);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, ID_FIELD);
    }   
}
