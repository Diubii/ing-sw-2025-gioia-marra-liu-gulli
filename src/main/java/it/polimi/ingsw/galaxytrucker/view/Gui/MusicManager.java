package it.polimi.ingsw.galaxytrucker.view.Gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class MusicManager {
    private static MusicManager instance;
    private Clip backgroundClip;

    protected MusicManager() {}

    public void playBackgroundMusic(String fileName, boolean loop) {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            return;
        }

        try {
            InputStream raw = getClass().getResourceAsStream("/it/polimi/ingsw/galaxytrucker/Sounds/Music/"+fileName);
            BufferedInputStream bufferedIn = new BufferedInputStream(raw);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            if (loop) {
                backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                backgroundClip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    public void restartBackgroundMusic() {
        if (backgroundClip != null) {
            backgroundClip.setFramePosition(0);
            backgroundClip.start();
        }
    }

    public boolean isPlaying() {
        return backgroundClip != null && backgroundClip.isRunning();
    }
}