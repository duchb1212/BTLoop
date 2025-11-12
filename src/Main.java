import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int width = 800;
            int height = 600;

            JFrame frame = new JFrame("Arkanoid");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(width, height);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            // Gắn panel menu vào frame
            frame.add(new MainMenuPanel(frame, width, height));
            frame.setVisible(true);
        });
    }
}
