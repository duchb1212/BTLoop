import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private static Clip backgroundClip;

    // Phát nhạc nền lặp vô hạn
    public static void playBackgroundMusic(String filePath) {
        stopBackgroundMusic(); // Dừng nhạc cũ nếu có

        new Thread(() -> {
            try {
                File soundFile = new File(filePath);
                if (!soundFile.exists()) {
                    System.err.println("Background music not found: " + filePath);
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(audioStream);
                backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // Lặp liên tục
                backgroundClip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Dừng nhạc
    public static void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    // Tiếp tục nhạc
    public static void resumeBackgroundMusic() {
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.start();
        }
    }

    public static void playSoundEffect(String filePath) {
        new Thread(() -> {
            try {
                File soundFile = new File(filePath);
                if (!soundFile.exists()) {
                    System.err.println("Sound file not found: " + filePath);
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumeBoost = 6.0f; // Tăng âm lượng thêm 6dB (khoảng gấp đôi)
                float newGain = Math.min(gainControl.getMaximum(), gainControl.getValue() + volumeBoost);
                gainControl.setValue(newGain);
                clip.start(); // phát 1 lần

                // Giải phóng tài nguyên sau khi phát xong
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Giải phóng tài nguyên khi thoát game
    public static void close() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }
}
