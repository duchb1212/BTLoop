public class Main {
    public static void main(String[] args) {
        // Ensure Swing UI is created on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            int width = 800;
            int height = 600;

            javax.swing.JFrame frame = new javax.swing.JFrame("Arkanoid");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            GamePanel gamePanel = new GamePanel(width, height);
            frame.add(gamePanel);

            frame.pack();
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);

            // Request focus so key events go to the panel
            gamePanel.requestFocusInWindow();
        });
    }
}