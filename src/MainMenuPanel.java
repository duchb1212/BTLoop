import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.regex.*;

public class MainMenuPanel extends JPanel {
    private int levelCount = 1;
    private JComboBox<Integer> levelSelector;

    public MainMenuPanel(JFrame frame, int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(new Color(24, 24, 36));
        setLayout(new GridBagLayout());

        // Main layout constraints - Căn giữa cả menu
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Menu panel - center content
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(48, 48, 80),
                        getWidth(), getHeight(), new Color(32, 32, 48, 220)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
            }
        };
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(32, 60, 32, 60));
        menuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("ARKANOID");
        title.setForeground(new Color(222, 196, 31));
        title.setFont(new Font("Arial", Font.BOLD, 56));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Selector
        File dir = new File(LevelLoader.getLevelDir());
        Pattern levelPattern = Pattern.compile("level(\\d+)\\.xml");
        int maxLevel = 1;
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                Matcher m = levelPattern.matcher(file.getName());
                if (m.matches()) {
                    int lv = Integer.parseInt(m.group(1));
                    if (lv > maxLevel) maxLevel = lv;
                }
            }
        }
        levelCount = maxLevel;
        Integer[] levels = new Integer[levelCount];
        for (int i=1; i<=levelCount; ++i) levels[i-1] = i;

        levelSelector = new JComboBox<>(levels);
        levelSelector.setSelectedIndex(0);
        levelSelector.setMaximumSize(new Dimension(180, 40));
        levelSelector.setFont(new Font("Arial", Font.BOLD, 22));
        levelSelector.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel levelPanel = new JPanel();
        levelPanel.setOpaque(false);
        levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.X_AXIS));
        levelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel selectLabel = new JLabel("Select Level:");
        selectLabel.setForeground(new Color(225,225,234));
        selectLabel.setFont(new Font("Arial", Font.BOLD, 22));
        levelPanel.add(selectLabel);
        levelPanel.add(Box.createHorizontalStrut(12));
        levelPanel.add(levelSelector);

        // --- Nút custom ---
        class ModernButton extends JButton {
            ModernButton(String text) {
                super(text);
                setFont(new Font("Arial", Font.BOLD, 22));
                setPreferredSize(new Dimension(240, 54));
                setMaximumSize(new Dimension(240, 54));
                setBackground(new Color(170,130,34));
                setForeground(Color.WHITE);
                setFocusPainted(false);
                setBorder(BorderFactory.createEmptyBorder(12,0,12,0));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setAlignmentX(Component.CENTER_ALIGNMENT);

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setBackground(new Color(222, 196, 31));
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        setBackground(new Color(170,130,34));
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {
                        setBackground(new Color(132,90,21));
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        setBackground(new Color(222, 196, 31));
                    }
                });
            }
        }

        ModernButton newGameButton     = new ModernButton("New Game");
        ModernButton continueButton    = new ModernButton("Continue");
        ModernButton highScoreButton   = new ModernButton("High Score");
        ModernButton exitButton        = new ModernButton("Exit");

        // Center all buttons (đều nằm giữa panel)
        Box buttonBox = Box.createVerticalBox();
        buttonBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonBox.add(newGameButton);
        buttonBox.add(Box.createVerticalStrut(15));
        if (SaveManager.saveExists("saves/quicksave.xml")) {
            buttonBox.add(continueButton);
            buttonBox.add(Box.createVerticalStrut(15));
        }
        buttonBox.add(highScoreButton);
        buttonBox.add(Box.createVerticalStrut(15));
        buttonBox.add(exitButton);

        // Sắp xếp giữa: Title, Selector, Nút
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(title);
        menuPanel.add(Box.createVerticalStrut(18));
        menuPanel.add(levelPanel);
        menuPanel.add(Box.createVerticalStrut(36));
        menuPanel.add(buttonBox);
        menuPanel.add(Box.createVerticalGlue());

        add(menuPanel, gbc);

        // --- Xử lý chức năng nút ---
        newGameButton.addActionListener((ActionEvent e) -> {
            int levelToPlay = (Integer) levelSelector.getSelectedItem();
            GameEngine engine = new GameEngine(width, height);
            engine.setCurrentLevel(levelToPlay);
            engine.restart();
            GamePanel gamePanel = new GamePanel(engine, width, height);
            gamePanel.setShowHighScore(true);
            frame.setContentPane(gamePanel);
            frame.revalidate();
            frame.repaint();
            gamePanel.requestFocusInWindow();
        });

        continueButton.addActionListener((ActionEvent e) -> {
            GameEngine engine = new GameEngine(width, height);
            boolean loaded = SaveManager.quickLoad(engine, width, height);
            if (loaded) {
                engine.setPaused(false);
                engine.setGameOver(false);
                engine.setGameWon(false);

                GamePanel gamePanel = new GamePanel(engine, width, height);
                gamePanel.setShowHighScore(true);
                gamePanel.hidePauseOverlay();
                frame.setContentPane(gamePanel);
                frame.revalidate();
                frame.repaint();
                gamePanel.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "No saved game found.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        highScoreButton.addActionListener((ActionEvent e) -> {
            int highScore = HighScoreManager.getHighScore();
            String msg = "High Score: " + highScore;
            JOptionPane.showMessageDialog(this, msg, "High Score", JOptionPane.INFORMATION_MESSAGE);
        });

        exitButton.addActionListener(e -> System.exit(0));
    }
}
