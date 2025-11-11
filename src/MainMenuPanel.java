import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainMenuPanel extends JPanel {
    public MainMenuPanel(JFrame frame, int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("ARKANOID");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton newGameButton = new JButton("New Game");
        JButton continueButton = new JButton("Continue");
        JButton exitButton = new JButton("Exit");

        Dimension btnSize = new Dimension(200, 50);
        JButton[] buttons = {newGameButton, continueButton, exitButton};
        for (JButton btn : buttons) {
            btn.setPreferredSize(btnSize);
            btn.setMaximumSize(btnSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.setFocusPainted(false);
        }

        menuPanel.add(title);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(newGameButton);
        menuPanel.add(Box.createVerticalStrut(15));

        // Kiểm tra file save trước khi thêm nút Continue
        if (SaveManager.saveExists("saves/quicksave.xml")) {
            menuPanel.add(continueButton);
            menuPanel.add(Box.createVerticalStrut(15));
        }

        menuPanel.add(exitButton);
        add(menuPanel, gbc);

        // --- Xử lý các nút ---
        newGameButton.addActionListener((ActionEvent e) -> {
            GamePanel gamePanel = new GamePanel(width, height);
            frame.setContentPane(gamePanel);
            frame.revalidate();
            frame.repaint();
            gamePanel.requestFocusInWindow();
        });

        continueButton.addActionListener((ActionEvent e) -> {
            GameEngine engine = new GameEngine(width, height);
            boolean loaded = SaveManager.quickLoad(engine, width, height);
            if (loaded) {
                GamePanel gamePanel = new GamePanel(width, height);
                gamePanel.setEngine(engine);
                frame.setContentPane(gamePanel);
                frame.revalidate();
                frame.repaint();
                gamePanel.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "No saved game found.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        exitButton.addActionListener(e -> System.exit(0));
    }
}
