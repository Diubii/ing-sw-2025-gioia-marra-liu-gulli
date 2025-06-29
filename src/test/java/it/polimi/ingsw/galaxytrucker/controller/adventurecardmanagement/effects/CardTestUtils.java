package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading and filtering adventure cards from a JSON resource file,
 * primarily used for testing purposes.
 */
public class CardTestUtils {

    /**
     * Loads a specific number of AdventureCard objects matching the given type name
     * from the 'cardsdata.json' file in the resources folder.
     *
     * @param typeName the name of the card type to filter by.
     * @param count the maximum number of cards to return.
     * @return a list of matching AdventureCard objects, or an empty list if none found or error occurs.
     */
    public static List<AdventureCard> loadCardsByType(String typeName, int count) {
        URL url = CardTestUtils.class.getClassLoader().getResource("cardsdata.json");

        File file = new File(url.getFile());

        ObjectMapper mapper = new ObjectMapper();

        ArrayList<AdventureCard> list = new ArrayList<>();

        try {
            list = mapper.readValue(file, new TypeReference<>() {});
        }
        catch (IOException e) {
            return list;
        }

        return list.stream()
                .filter(card -> card.getName().equals(typeName))
                .limit(count)
                .toList();
    }
}
