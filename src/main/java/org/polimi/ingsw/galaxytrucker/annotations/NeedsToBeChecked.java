package org.polimi.ingsw.galaxytrucker.annotations;

/**
 * Marks methods that need to be checked to clear doubts. A message can be attached.
 * @author Alessandro Giuseppe Gioia
 */
public @interface NeedsToBeChecked {
    String value() default "";
}
