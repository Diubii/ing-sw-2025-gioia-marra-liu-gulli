package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class NicknameRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1062869860895077647L;

    private String nickname;

    // ✅ Costruttore senza argomenti richiesto per la deserializzazione
    public NicknameRequest() {
        super(); // o un default valido
    }

    // ✅ Costruttore che usi per creare il messaggio nel client
    public NicknameRequest(String nickname) {
        super();
        this.nickname = nickname;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public String getNickname() {
        return nickname;
    }


    // Setter necessari per deserializzazione
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
