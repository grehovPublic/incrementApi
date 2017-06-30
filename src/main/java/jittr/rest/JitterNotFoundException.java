package jittr.rest;


/**
 * 
 * @author Grehov
 *
 */
class JitterNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JitterNotFoundException(String userId) {
        super("Could not find user '" + userId + "'.");
    }
}
