package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

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
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, ExecutionException, InterruptedException {
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
