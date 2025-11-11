import javax.swing.*;
import javafx.application.Platform;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int width = 800;
            int height = 600;

            // Khởi động JavaFX runtime trước
            FXInitializer.initFX();

            JFrame frame = new JFrame("Arkanoid");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(width, height);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            // Hiển thị menu chính bằng JavaFX
            Platform.runLater(() -> MainMenuFX.showFXMenu(frame, width, height));

            frame.setVisible(true);
        });
    }
}
