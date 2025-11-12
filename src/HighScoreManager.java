import java.io.*;

public class HighScoreManager {
    private static final String SAVE_PATH = "saves/highscore.dat";
    private static int highScore = 0;

    static {
        load();
    }

    public static int getHighScore() {
        return highScore;
    }

    public static void checkAndSetHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            save();
        }
    }

    public static void save() {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(SAVE_PATH))) {
            dos.writeInt(highScore);
        } catch (IOException e) {
            System.err.println("Không thể lưu high score!");
        }
    }

    public static void load() {
        File file = new File(SAVE_PATH);
        if (!file.exists()) {
            highScore = 0;
            return;
        }
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            highScore = dis.readInt();
        } catch (IOException e) {
            highScore = 0;
        }
    }

    public static void reset() {
        highScore = 0;
        save();
    }
}
