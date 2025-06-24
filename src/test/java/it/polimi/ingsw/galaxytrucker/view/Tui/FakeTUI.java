package it.polimi.ingsw.galaxytrucker.view.Tui;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;

import java.io.PrintStream;

public class FakeTUI extends Tui{

    public FakeTUI(PrintStream out, Boolean isSocket, ClientController controller) {
        super(out, isSocket, controller);
    }

    //
//        return input.trim();
//    }
    @Override
    public String readLine(String prompt) {
        return "RESET";
    }
}
