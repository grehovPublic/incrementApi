package jittr.rest;


/**
 * 
 * @author Grehov
 *
 */
class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String id) {
        super("Could not find entity '" + id + "'.");
    }
}
