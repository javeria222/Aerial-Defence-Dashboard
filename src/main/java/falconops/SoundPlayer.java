package falconops;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class SoundPlayer {
    private static Clip loopingClip; // To control continuous sounds

    public static void play(String fileName, boolean loop) {
        try {
            URL soundURL = SoundPlayer.class.getResource("/" + fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                loopingClip = clip; // Store reference to stop later
            } else {
                clip.start();
            }

        } catch (Exception e) {
            System.out.println("Sound error: " + e.getMessage());
        }
    }

    public static void stopLoopingSound() {
        if (loopingClip != null && loopingClip.isRunning()) {
            loopingClip.stop();
            loopingClip.close();
            loopingClip = null;
        }
    }
}
