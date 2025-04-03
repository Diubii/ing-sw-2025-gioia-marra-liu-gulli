package org.polimi.ingsw.galaxytrucker.view;

import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public  interface View  {
    void askNickname() throws IOException, ExecutionException, InterruptedException;

//    public void askNickname(Thread thread) throws IOException;
}
