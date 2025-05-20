package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardTestUtils {

    public static List<AdventureCard> loadCardsByType(String typeName, int count) throws IOException {
        File file = new File("src/main/resources/cardsdata.json");
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<AdventureCard> list = mapper.readValue(file, new TypeReference<>() {
        });

        return list.stream()
                .filter(card -> card.getName().equals(typeName))
                .limit(count)
                .toList();
    }
}
