package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.Serial;
import java.io.Serializable;

public class NUM_PLAYERS_REQUEST extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1062869860895077648L;
    private Boolean learningMatch;
    private Integer num;


    // ✅ Costruttore senza argomenti richiesto per la deserializzazione
    public NUM_PLAYERS_REQUEST() {
        super(); // o un default valido
    }

    // ✅ Costruttore che usi per creare il messaggio nel client
    public NUM_PLAYERS_REQUEST(Integer num, Boolean learningMatch) {
        super();
        this.learningMatch = learningMatch;
        this.num = num;
    }

    public NetworkMessageType accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }

    public Integer getNum() {
        return num;
    }

    public Boolean getLearningMatch() {
        return learningMatch;
    }


}
