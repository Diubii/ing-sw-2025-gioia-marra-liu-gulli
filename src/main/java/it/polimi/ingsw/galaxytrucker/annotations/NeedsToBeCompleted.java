package it.polimi.ingsw.galaxytrucker.annotations;

/**
 * Marks methods that aren't yet complete.
 *
 * @author Alessandro Giuseppe Gioia
 */
public @interface NeedsToBeCompleted {
    String value() default "";
}
