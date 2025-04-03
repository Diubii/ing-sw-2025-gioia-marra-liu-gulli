package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.Serial;
import java.io.Serializable;

public class NICKNAME_REQUEST extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1062869860895077647L;

    private String nickname;
    private Boolean learningMatch = null;

    // ✅ Costruttore senza argomenti richiesto per la deserializzazione
    public NICKNAME_REQUEST() {
        super(NetworkMessageType.NICKNAME_REQUEST); // o un default valido
    }

    // ✅ Costruttore che usi per creare il messaggio nel client
    public NICKNAME_REQUEST(NetworkMessageType type, String nickname, Boolean flag) {
        super(type);
        this.nickname = nickname;
        this.learningMatch = flag;
    }

    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }

    public String getNickname() {
        return nickname;
    }

    public Boolean getLearningMatch() {
        return learningMatch;
    }

    // Setter necessari per deserializzazione
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLearningMatch(Boolean learningMatch) {
        this.learningMatch = learningMatch;
    }
}
