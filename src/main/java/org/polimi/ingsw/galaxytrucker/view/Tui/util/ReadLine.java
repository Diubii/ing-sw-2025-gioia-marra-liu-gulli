package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.view.Tui.InputManagerTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ReadLine {


    public static String run(Thread inputThread) throws ExecutionException {
        FutureTask<String> task = new FutureTask<>(new InputManagerTask());
        inputThread = new Thread(task);
        inputThread.start();

        try {
            return task.get();
        } catch (InterruptedException e) {
            task.cancel(true);
            Thread.currentThread().interrupt();
            return null;
        }
    }
}

