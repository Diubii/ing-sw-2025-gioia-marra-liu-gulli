package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import java.io.Serial;
import java.io.Serializable;

public class LOBBY_INFO extends NetworkMessage  implements Serializable {

    @Serial
    private static final long serialVersionUID = 2022869431895077642L;
    private Integer nMaxPlayers = 4;
    private Boolean isFirst;

    public LOBBY_INFO() {
        super(NetworkMessageType.LOBBY_INFO);

    }

    public Integer getnMaxPlayers() {
        return nMaxPlayers;
    }
    public void setnMaxPlayers(Integer nMaxPlayers) {
        this.nMaxPlayers = nMaxPlayers;
    }
    public Boolean getIsFirst() {
        return isFirst;
    }
    public void setIsFirst(Boolean isFirst) {
        this.isFirst = isFirst;
    }


    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }

}
