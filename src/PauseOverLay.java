import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PauseOverLay extends JPanel {

    private final JButton resumeButton;
    private final JButton restartButton;
    private final JButton saveButton;
    private final JButton backToMenuButton;

    public PauseOverLay(GamePanel gamePanel, GameEngine engine) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        // Cấu hình layout căn giữa
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JLabel pauseLabel = new JLabel("PAUSED");
        pauseLabel.setForeground(Color.WHITE);
        pauseLabel.setFont(new Font("Arial", Font.BOLD, 40));
        pauseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resumeButton = new JButton("Resume");
        restartButton = new JButton("Restart");
        saveButton = new JButton("Save");
        backToMenuButton = new JButton("Back to Menu");

        Dimension btnSize = new Dimension(220, 50);
        JButton[] buttons = {resumeButton, restartButton, saveButton, backToMenuButton};
        for (JButton btn : buttons) {
            btn.setPreferredSize(btnSize);
            btn.setMaximumSize(btnSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.setFocusPainted(false);
        }

        buttonPanel.add(pauseLabel);
        buttonPanel.add(Box.createVerticalStrut(40));
        buttonPanel.add(resumeButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(restartButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(backToMenuButton);

        add(buttonPanel, gbc);

        // --- Các nút chức năng ---

        // Resume game
        resumeButton.addActionListener((ActionEvent e) -> {
            engine.togglePaused();
            gamePanel.hidePauseOverlay();
        });

        // Restart game
        restartButton.addActionListener((ActionEvent e) -> {
            engine.restart();
            gamePanel.hidePauseOverlay();
        });

        // Save game
        saveButton.addActionListener((ActionEvent e) -> {
            java.io.File dir = new java.io.File("saves");
            if (!dir.exists()) dir.mkdirs();

            boolean success = SaveManager.quickSave(engine);
            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Game saved successfully!",
                        "Save",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to save game!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Back to main menu
        backToMenuButton.addActionListener((ActionEvent e) -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gamePanel);
            if (frame != null) {
                frame.setContentPane(new MainMenuPanel(frame, gamePanel.getWidth(), gamePanel.getHeight()));
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Nền mờ trong suốt phủ lên game
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 150)); // đen, alpha = 150/255
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
