import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * GamePanel: UI layer, handles input, ticking and rendering.
 * Adjusted to match updated APIs (camelCase method names, updated brick types).
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private GameEngine gameEngine;
    private Renderer renderer;
    private Timer timer;
    private PauseOverLay pauseOverlay;
    private boolean isPaused = false;
    private Image backgroundTexture;

    private boolean pressedLeft = false;
    private boolean pressedRight = false;
    private GameEngine engine;

    public GamePanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Load background texture
        try {
            backgroundTexture = ImageIO.read(getClass().getClassLoader()
                    .getResource("textures/Background.jpg"));
        } catch (IOException e) {
            System.err.println("Không thể load background texture!");
        }

        gameEngine = new GameEngine(width, height);
        renderer = new Renderer();

        // Pause Overlay
        pauseOverlay = new PauseOverLay(this, gameEngine);
        pauseOverlay.setBounds(0, 0, width, height);
        pauseOverlay.setVisible(false);
        setLayout(null);
        add(pauseOverlay);

        timer = new Timer(1000 / 60, this);
        timer.start();

        SoundManager.playBackgroundMusic("src/sounds/Music.wav");

    }

    public void togglePause() {
        isPaused = !isPaused;
        gameEngine.setPaused(isPaused);
        pauseOverlay.setVisible(isPaused);
        repaint();
    }

    public void hidePauseOverlay() {
        isPaused = false;
        gameEngine.setPaused(false);
        pauseOverlay.setVisible(false);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Render background ---
        if (backgroundTexture != null) {
            g2d.drawImage(backgroundTexture, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(Color.RED); // fallback nếu texture không load được
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Render paddle
        var paddle = gameEngine.getPaddle();
        if (paddle.getTexture() != null) {
            g2d.drawImage(
                    paddle.getTexture(),
                    (int) paddle.getPosX(),
                    (int) paddle.getPosY(),
                    (int) paddle.getWidth(),
                    (int) paddle.getHeight(),
                    null
            );
        }

        // Render ball
        for (var ball : gameEngine.getBalls()) {
            if (ball.getTexture() != null) {
                g2d.drawImage(
                        ball.getTexture(),
                        (int) ball.getPosX(),
                        (int) ball.getPosY(),
                        (int) ball.getWidth(),
                        (int) ball.getHeight(),
                        null
                );
            }
        }

        // Render bricks (only those not destroyed)
        for (var brick : gameEngine.getBricks()) {
            if (!brick.isDestroyed()) {
                if (brick.getTexture() != null) {
                    g2d.drawImage(
                            brick.getTexture(),
                            (int) brick.getPosX(),
                            (int) brick.getPosY(),
                            (int) brick.getWidth(),
                            (int) brick.getHeight(),
                            null
                    );
                }
            }
        }

        for (var powerball : gameEngine.getBuffs()) {
            if (powerball instanceof Buff buff) {
                Image tex = buff.getBuffType().getTexture();
                if (tex != null) {
                    g.drawImage(tex,
                            (int) buff.getPosX(),
                            (int) buff.getPosY(),
                            (int) buff.getWidth(),
                            (int) buff.getHeight(),
                            null);
                }
            }
        }

        renderUI(g);
    }

    private void renderUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Score: " + gameEngine.getScore(), 10, 20);
        g.drawString("Lives: " + gameEngine.getLives(), 10, 40);

        int lineHeight = 50;
        if (gameEngine.isGameOver()) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOver = "Game Over";
            int textWidth = g.getFontMetrics().stringWidth(gameOver);
            g.drawString(gameOver, (getWidth() - textWidth) / 2, getHeight() / 2);

            String restartGame = "Press R to restart";
            int textWidth2 = g.getFontMetrics().stringWidth(restartGame);
            g.drawString(restartGame, (getWidth() - textWidth2) / 2, getHeight() / 2 + lineHeight);
        } else if (gameEngine.isGameWon()) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String gameWon = "Game Won";
            int textWidth = g.getFontMetrics().stringWidth(gameWon);
            g.drawString(gameWon, (getWidth() - textWidth) / 2, getHeight() / 2);

            String restartGame = "Press R to restart";
            int textWidth2 = g.getFontMetrics().stringWidth(restartGame);
            g.drawString(restartGame, (getWidth() - textWidth2) / 2, getHeight() / 2 + lineHeight);
        }
    }

    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }


    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (pressedLeft && !pressedRight) {
            gameEngine.getPaddle().moveLeft();
        } else if (pressedRight && !pressedLeft) {
            gameEngine.getPaddle().moveRight();
        } else {
            gameEngine.getPaddle().stop();
        }
        // Pass deltaTime as a fixed timestep (1/60s) to engine update for consistency.
        gameEngine.update(1.0 / 60.0);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            if (!gameEngine.getBalls().get(0).isLaunched()) {
                // initial launch direction: slightly to the right and upward
                gameEngine.getBalls().get(0).launch(0.2, -1.0);
            }
        }
        if (key == KeyEvent.VK_ESCAPE) {
            togglePause();
        }

        if (key == KeyEvent.VK_LEFT) {
            pressedLeft = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            pressedRight = true;
        }
        if (key == KeyEvent.VK_P) {
            gameEngine.togglePaused();
        }
        if (key == KeyEvent.VK_R) {
            if (gameEngine.isGameOver() || gameEngine.isGameWon()) {
                gameEngine.restart();
            }
        }
        // Quick Save - F5
        else if (key == KeyEvent.VK_F5) {
            if (SaveManager.quickSave(gameEngine)) {
                System.out.println("✓ Quick Save successful!");
            } else {
                System.out.println("✗ Quick Save failed!");
            }
        }
        // Quick Load - F9
        else if (key == KeyEvent.VK_F9) {
            if (SaveManager.quickLoad(gameEngine, getWidth(), getHeight())) {
                System.out.println("✓ Quick Load successful!");
            } else {
                System.out.println("✗ Quick Load failed!");
            }
        }
        // Save to slot 1-9 - Ctrl+1 through Ctrl+9
        else if (e.isControlDown() && key >= KeyEvent.VK_1 && key <= KeyEvent.VK_9) {
            int slot = key - KeyEvent.VK_0; // Convert key to number 1-9
            if (SaveManager.saveToSlot(slot, gameEngine)) {
                System.out.println("✓ Saved to slot " + slot);
            } else {
                System.out.println("✗ Failed to save to slot " + slot);
            }
        }
        // Load from slot 1-9 - Alt+1 through Alt+9
        else if (e.isAltDown() && key >= KeyEvent.VK_1 && key <= KeyEvent.VK_9) {
            int slot = key - KeyEvent.VK_0; // Convert key to number 1-9
            if (SaveManager.loadFromSlot(slot, gameEngine, getWidth(), getHeight())) {
                System.out.println("✓ Loaded from slot " + slot);
            } else {
                System.out.println("✗ Failed to load from slot " + slot);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            pressedLeft = false;
        }
        if (key == KeyEvent.VK_RIGHT) {
            pressedRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
