package org.polimi.ingsw.galaxytrucker.annotations;

/**
 * Marks methods whose type is temporary and will need to be replaced.
 *
 * @author Alessandro Giuseppe Gioia
 */
public @interface TemporaryType {
    String temporaryType();

    String actualType();
}
