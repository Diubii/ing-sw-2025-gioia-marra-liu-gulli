package it.polimi.ingsw.galaxytrucker.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class Hourglass implements Serializable {
    @Serial
    private static final long SerialVersionUID = 1019019019L;

    private boolean canRotate = true;
    private Timer timer = new Timer();

    public void startCooldown(long delayMillis) {
        if (!canRotate) {
            return;
        }

        canRotate = false;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                canRotate = true;
            }
        }, delayMillis);
    }

    public boolean isCanRotate() {
        return canRotate;
    }

}
