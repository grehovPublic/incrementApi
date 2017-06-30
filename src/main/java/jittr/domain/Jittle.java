package jittr.domain;

import static jittr.domain.SharedConstants.*;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This datatype is based on a tweet from Twitter,
 * graded as negative/neutral/positive by a trained R model.
 */
@Entity
public class Jittle {
    
    protected static final String ID_JTL_GENERATOR = "ID_JITTLES_GENERATOR";
    protected static final String JTL_SEQ = "jittles_seq";   
    protected static final String COLNAME_JITTLE = "jittle"; 
    protected static final String COLNAME_POSTED_TIME = "postedtime"; 
    protected static final String COLNAME_TQUEUE = "tqueue"; 
    
    public static final String FIELD_EMAIL = "email";

    @Id
//    @GeneratedValue(generator = ID_JTL_GENERATOR)
//    @GenericGenerator(name = ID_JTL_GENERATOR, strategy = ENHANCED_SEQ,
//        parameters = { @Parameter(name = "sequence_name", value = JTL_SEQ)})
    private Long id;

    @ManyToOne
    @JoinColumn(name = COLNAME_JITTER)
    private Jitter jitter;

    @Column
    private String message;

    @Column(name = COLNAME_POSTED_TIME)
    private Date postedTime;
    
    @Column
    private String author;

    /**
     *  Attitude to the subject from the tweet message.
     *  Evaluated by the trained R model.
     */
    public enum Judgment  {VERY_NEGATIVE, NEGATIVE, NEUTRAL, NONE,
        POSITIVE, VERY_POSITIVE}
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private Judgment judgment;
    
    /**
     * Types of queues, holding {@link Jittle}.
     */
    public enum TargetQueue {TRAIN_RAW, TRAIN_GRADED, BUILD_MAP, VIEW_RAW, VIEW_GRADED};
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = COLNAME_TQUEUE)
    private TargetQueue tQueue;

    @Column
    private String country;

    @Column
    private Float latitude;

    @Column
    private Float longitude;

    /**
     * Default constructor for JPA.
     */
    private Jittle() {
        this.setJudgment(Judgment.NONE);
        this.latitude = Float.NaN;
        this.longitude = Float.NaN;
        this.country = "";
    }

    /**
     * Make a {@link Jittle} with a known unique id.
     * 
     * @param id
     *            unique identifier for the tweet, as assigned by Twitter.
     * @param jitter
     *            User, owner of the jittle.
     * @param message
     *            text of the tweet, at most 140 characters.
     * @param postedTime
     *            date/time when the tweet was sent.
     * @param author
     *            Twitter username who wrote this tweet.  
     *            Required to be a Twitter username as defined by getAuthor().
     * @param country
     *            Twitter user's country, specified in original tweet.
     */
    public Jittle(final Long id, final Jitter jitter, final String message, 
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
     * @return unique identifier of this {@link Jittle}.
     * Based on the original tweet's id.
     */
    public Long getId() { return this.id; }

    /**
     * Sets identifier of this {@link Jittle}.
     * @param id unique identifier of this {@link Jittle}. Based on the original tweet's id.
     * 
     */
    public void setId(final Long id) { this.id = id; }

    /**
     * @return text of this {@link Jittle}, at most 140 characters.
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Sets text of this {@link Jittle}, at most 140 characters.
     * 
     * @param message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @return date/time when the original tweet was sent.
     */
    public Date getPostedTime() { return this.postedTime; }
    
    /**
     * Sets date/time when the original tweet was sent.
     * 
     * @param postedTime date/time when the original tweet was sent.
     */
    public void setPostedTime(Date postedTime) { this.postedTime = postedTime; }
    
    /**
     * @return Twitter username who wrote this tweet.
     *         A Twitter username is a nonempty sequence of letters (A-Z or
     *         a-z), digits, underscore ("_"), or hyphen ("-").
     *         Twitter usernames are case-insensitive, so "jbieber" and "JBieBer"
     *         are equivalent.
     */
    public String getAuthor() { return this.author; }
    
    /**
     * Sets the author of original tweet.
     * 
     * @param author the author of original tweet. See {@link Jittle#getAuthor()}
     */
    public void setAuthor(final String author) { this.author = author; }

    /**
     * @return the {@link Jitter}, owner of this {@link Jittle}.
     */
    public Jitter getJitter() { return this.jitter; }
   
    /**
     * Sets the {@link Jitter}, owner of this {@link Jittle}.
     * 
     * @param jitter {@link Jitter} to be set.
     */
    public void setJitter(Jitter jitter) { this.jitter = jitter; }

    /**
     * @return grade of this {@link Jittle}.
     */
    public Judgment getJudgment() { return this.judgment; }

    /**
     * Sets {@link Jittle#judgment} for this {@link Jittle}. Default grade 'none'.
     * 
     * @param judgment
     *          positive/neutral/negative grade.            
     */
    public void setJudgment(final Judgment judgment) {
        this.judgment = judgment;
    }
    
    /**
     * @return target queue owning this {@link Jittle}.
     */
    public TargetQueue getTQueue() { return this.tQueue; }

    /**
     * Set target queue for this {@link Jittle}.
     * 
     * @param tQueue
     *           target queue owning this {@link Jittle}.            
     */
    public void setTQueue(final TargetQueue tQueue) {
        this.tQueue = tQueue;
    }

    /**
     * Gets the country of origin of the original tweet.
     * 
     * @return Twitter country of this tweet.
     *         A Twitter country is empty if not defined.
     *         Twitter country is case-insensitive, so "USA" and "usa"
     *         are equivalent.
     */
    public String getCountry() { return this.country; }
    
    /**
     * Sets the country of origin of the original tweet.

     * @param country. See {@link Jittle#getCountry()}
     */
    public void setCountry(final String country) { this.country = country; }

    /**
     * Sets the latitude of the original tweet.
     * 
     * @return latitude of this {@link Jittle}. Float.NaN if not defined.
     */
    public Float getLatitude() { return this.latitude; }
    
    /**
     * Sets the latitude of the original tweet.
     * 
     * @param latitude see {@link Jittle#getLatitude()}.
     */
    public void setLatitude(final Float latitude) { this.latitude = latitude; }

    /**
     * @return longitude of this {@link Jittle}. Float.NaN if not defined.
     */
    public Float getLongitude() { return this.longitude; }
    
    /**
     * Sets the longitude of the original tweet.
     * 
     * @param longitude see {@link Jittle#getLongitude()}.
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
    
    @Override
    public String toString() {
        return id.toString() + " " + message.toString();
    }

}
