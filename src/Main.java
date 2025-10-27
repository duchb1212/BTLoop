import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int width = 800;
        int height = 600;

        JFrame frame = new JFrame("Arkanoid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel gamePanel = new GamePanel(width, height);
        frame.add(gamePanel);

        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}
