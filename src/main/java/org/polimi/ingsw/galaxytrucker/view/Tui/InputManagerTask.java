package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class InputManagerTask implements Callable<String> {
    private final BufferedReader reader;

    public InputManagerTask() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public String call() throws IOException, InterruptedException {
        while (!isInputAvailable()) {
            Thread.sleep(200);
        }
        return reader.readLine();
    }

    private boolean isInputAvailable() throws IOException {
        return reader.ready();
    }
}
