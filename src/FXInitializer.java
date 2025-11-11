import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class FXInitializer {
    private static boolean initialized = false;

    public static void initFX() {
        if (!initialized) {
            new JFXPanel(); // Kích hoạt JavaFX runtime trong Swing
            initialized = true;
            Platform.setImplicitExit(false); // Không đóng khi stage tạm tắt
        }
    }
}
