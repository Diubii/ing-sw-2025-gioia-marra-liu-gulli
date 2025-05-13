package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.model.essentials.Position;

import java.util.Arrays;
import java.util.List;

public class InputUtils {


    public static Position parseCoordinate(String input) throws IllegalArgumentException {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input is null or blank");
        }

        // Rimuove parentesi tonde e spazi
        String cleaned = input.replaceAll("[()\\s]", "");

        // Split sui caratteri ','
        String[] parts = cleaned.split(",");

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("Invalid coordinate format. Expected format: (x,y)");
        }

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return new Position(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be valid integers.");
        }
    }

    private static final List<Integer> VALID_ROTATIONS = Arrays.asList(90, 180, 270, 360);

    public static int parseRotation(String input) throws IllegalArgumentException {
        try {
            int rotation = Integer.parseInt(input.trim());

            if (!VALID_ROTATIONS.contains(rotation)) {
                throw new IllegalArgumentException("Invalid rotation. Please enter 90, 180, 270, or 360.");
            }

            return rotation;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid number.");
        }
    }

    public static boolean parseYesNo(String input) throws IllegalArgumentException {
        input = input.trim().toLowerCase();
        if (input.equals("y") || input.equals("yes")) {
            return true;
        } else if (input.equals("n") || input.equals("no")) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid input. Please type 'y' or 'n'.");
        }
    }
}
