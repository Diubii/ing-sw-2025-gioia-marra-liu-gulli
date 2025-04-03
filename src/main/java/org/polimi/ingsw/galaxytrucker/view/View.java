package org.polimi.ingsw.galaxytrucker.view;

import org.polimi.ingsw.galaxytrucker.observer.Observable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public  interface View  {
    void askNickname() throws IOException, ExecutionException, InterruptedException;

    void showGenericMessage(String s);

    void askMaxPlayers() throws ExecutionException, InterruptedException, IOException;

//    public void askNickname(Thread thread) throws IOException;
}
