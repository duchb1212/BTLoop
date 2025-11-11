import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javax.swing.*;

public class MainMenuFX {

    public static Scene createSceneForJFXPanel(JFrame frame, int width, int height) {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        menu.setStyle("-fx-background-color: linear-gradient(to bottom, #111, #333); -fx-padding: 40;");

        Button newGameBtn = new Button("New Game");
        Button continueBtn = new Button("Continue");
        Button exitBtn = new Button("Exit");

        for (Button b : new Button[]{newGameBtn, continueBtn, exitBtn}) {
            b.setPrefWidth(200);
            b.setStyle("-fx-font-size:18px; -fx-background-color:#444; -fx-text-fill:white;");
        }

        // New Game
        newGameBtn.setOnAction(e -> {
            Platform.runLater(() -> {
                SwingUtilities.invokeLater(() -> {
                    frame.getContentPane().removeAll();
                    GamePanel gamePanel = new GamePanel(width, height);
                    frame.add(gamePanel);
                    frame.revalidate();
                    frame.repaint();

                    // Đảm bảo GamePanel lấy focus để nghe sự kiện phím
                    gamePanel.requestFocusInWindow();
                });
            });
        });

        // Continue (load từ save file)
        continueBtn.setOnAction(e -> {
            Platform.runLater(() -> {
                SwingUtilities.invokeLater(() -> {
                    frame.getContentPane().removeAll();
                    GamePanel gp = new GamePanel(width, height);
                    SaveManager.quickLoad(gp.getEngine(), width, height);
                    frame.add(gp);
                    frame.revalidate();
                    frame.repaint();

                    // Đảm bảo GamePanel lấy focus để nghe sự kiện phím
                    gp.requestFocusInWindow();
                });
            });
        });

        exitBtn.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        menu.getChildren().addAll(newGameBtn, continueBtn, exitBtn);
        return new Scene(menu, width, height, Color.BLACK);
    }

    public static void showFXMenu(JFrame frame, int width, int height) {
        new JFXPanel(); // khởi động môi trường JavaFX nếu chưa có
        SwingUtilities.invokeLater(() -> {
            JFXPanel menuPanel = new JFXPanel();
            frame.getContentPane().removeAll();
            frame.add(menuPanel);
            frame.revalidate();
            frame.repaint();

            Platform.runLater(() -> {
                Scene scene = createSceneForJFXPanel(frame, width, height);
                menuPanel.setScene(scene);
            });
        });
    }
}
