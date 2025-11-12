import javafx.application.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private GameEngine gameEngine;
    private Renderer renderer;
    private Timer timer;
    private boolean pressedLeft = false;
    private boolean pressedRight = false;

    // Overlay cho màn Pause
    private PauseOverlayPanel pauseOverlay;

    public GamePanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        gameEngine = new GameEngine(width, height);
        renderer = new Renderer();

        // 60 FPS update loop
        timer = new Timer(1000 / 60, this);
        timer.start();

        // Nhạc nền
        SoundManager.playBackgroundMusic(
                "src/sounds/Music.wav"
        );
    }

    // Getter/Setter cho SaveManager
    public GameEngine getEngine() {
        return gameEngine;
    }

    public void setEngine(GameEngine engineLoaded) {
        this.gameEngine = engineLoaded;
    }

    // Pause overlay setter/getter
    public void setPauseOverlay(PauseOverlayPanel overlay) {
        if (pauseOverlay != null) {
            this.remove(pauseOverlay); // Xoá overlay cũ nếu có
        }
        this.pauseOverlay = overlay;
        if (overlay != null) {
            this.setLayout(null); // Để overlay đặt vị trí tuyệt đối
            overlay.setBounds(0, 0, getWidth(), getHeight());
            this.add(overlay);
            this.setComponentZOrder(overlay, 0); // Đảm bảo overlay ở trên
            overlay.requestFocusInWindow();
        }
        repaint();
    }
    public PauseOverlayPanel getPauseOverlay() { return pauseOverlay; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paddle
        renderer.render(g, gameEngine.getPaddle(), Color.BLUE);

        // Balls
        for (var ball : gameEngine.getBalls()) {
            if (ball.getTexture() != null) {
                g2d.drawImage(ball.getTexture(),
                        (int) ball.getPosX(),
                        (int) ball.getPosY(),
                        (int) ball.getWidth(),
                        (int) ball.getHeight(),
                        null);
            }
        }

        // Bricks
        for (var brick : gameEngine.getBricks()) {
            if (!brick.isDestroyed()) {
                if (brick.getTexture() != null) {
                    g2d.drawImage(brick.getTexture(),
                            (int) brick.getPosX(),
                            (int) brick.getPosY(),
                            (int) brick.getWidth(),
                            (int) brick.getHeight(),
                            null);
                }
                renderer.render(g, brick, Color.GREEN);
            }
        }

        // Power-ups
        for (var powerball : gameEngine.getPowerUps()) {
            Buff powerBall = (Buff) powerball;
            Color color;
            switch (powerBall.getBuffType()) {
                case Fire_Ball -> color = new Color(255, 60, 60);
                case Enlarged_Ball -> color = Color.WHITE;
                case Split_Ball -> color = new Color(255, 0, 255);
                case Heart_Ball -> color = new Color(255, 0, 150);
                default -> color = Color.YELLOW;
            }
            renderer.render(g, powerball, color);
        }

        renderUI(g);

        // Không cần tự vẽ overlay nữa!
    }

    private void renderUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Score: " + gameEngine.getScore(), 10, 20);
        g.drawString("Lives: " + gameEngine.getLives(), 10, 40);

        int lineHeight = 50;
        if (gameEngine.isGameOver()) {
            drawCenteredText(g, "Game Over", lineHeight);
        } else if (gameEngine.isGameWon()) {
            drawCenteredText(g, "Game Won", lineHeight);
        }
    }

    private void drawCenteredText(Graphics g, String mainText, int lineHeight) {
        g.setFont(new Font("Arial", Font.BOLD, 48));
        int textWidth = g.getFontMetrics().stringWidth(mainText);
        g.drawString(mainText, (getWidth() - textWidth) / 2, getHeight() / 2);

        String restartGame = "Press R to restart";
        int textWidth2 = g.getFontMetrics().stringWidth(restartGame);
        g.drawString(restartGame, (getWidth() - textWidth2) / 2, getHeight() / 2 + lineHeight);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (!gameEngine.isPaused()) {
            if (pressedLeft && !pressedRight) {
                gameEngine.getPaddle().moveLeft();
            } else if (pressedRight && !pressedLeft) {
                gameEngine.getPaddle().moveRight();
            } else {
                gameEngine.getPaddle().stop();
            }
            gameEngine.update(1.0 / 60.0);
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE && !gameEngine.getBalls().get(0).isLaunched()) {
            gameEngine.getBalls().get(0).launch(0.2, -1.0);
        }

        if (key == KeyEvent.VK_ESCAPE) {
            if (!gameEngine.isPaused()) {
                gameEngine.togglePaused();
                setPauseOverlay(new PauseOverlayPanel(this, gameEngine));
            }
        }

        // Di chuyển
        if (key == KeyEvent.VK_LEFT) pressedLeft = true;
        if (key == KeyEvent.VK_RIGHT) pressedRight = true;

        // Restart khi thua/thắng
        if (key == KeyEvent.VK_R && (gameEngine.isGameOver() || gameEngine.isGameWon())) {
            gameEngine.restart();
        }

        // Quick save/load
        if (key == KeyEvent.VK_F5) SaveManager.quickSave(gameEngine);
        if (key == KeyEvent.VK_F9) SaveManager.quickLoad(gameEngine, getWidth(), getHeight());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) pressedLeft = false;
        if (key == KeyEvent.VK_RIGHT) pressedRight = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
