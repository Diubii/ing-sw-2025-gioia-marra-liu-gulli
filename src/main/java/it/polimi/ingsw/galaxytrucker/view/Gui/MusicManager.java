package it.polimi.ingsw.galaxytrucker.view.Gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Singleton-style manager class for handling background music playback in the GUI.
 * <p>
 * Supports looping or one-time playback of music files located in the
 * `/Sounds/Music/` resource directory. Also provides utility methods for
 * stopping music and checking if music is currently playing.
 */

public class MusicManager {
    private static MusicManager instance;
    private Clip backgroundClip;

    /**
     * Protected constructor to enforce singleton usage.
     */
    protected MusicManager() {}

    /**
     * Plays a background music file from the resources directory.
     *
     * @param fileName The name of the music file (e.g., "main_theme.wav").
     * @param loop     Whether the music should loop continuously.
     */
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

    /**
     * Stops the currently playing background music if it is running.
     */
    public void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }


    public boolean isPlaying() {
        return backgroundClip != null && backgroundClip.isRunning();
    }
}