import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * GamePanel: UI layer, handles input, ticking and rendering.
 * Adjusted to match updated APIs (camelCase method names, updated brick types).
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private GameEngine gameEngine;
    private Renderer renderer;
    private Timer timer;

    private boolean pressedLeft = false;
    private boolean pressedRight = false;

    public GamePanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        gameEngine = new GameEngine(width, height);
        renderer = new Renderer();

        timer = new Timer(1000 / 60, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Render paddle and ball
        renderer.render(g, gameEngine.getPaddle(), Color.BLUE);
        renderer.render(g, gameEngine.getBall(), Color.RED);

        // Render bricks (only those not destroyed)
        for (var brick : gameEngine.getBricks()) {
            if (!brick.isDestroyed()) {
                Color brickColor;
                if (brick instanceof StrongBrick) {
                    int hp = brick.getHitPoints();
                    if (hp >= 3) {
                        brickColor = new Color(139, 0, 0);
                    } else if (hp == 2) {
                        brickColor = new Color(178, 34, 34);
                    } else {
                        brickColor = new Color(205, 92, 92);
                    }
                } else {
                    brickColor = Color.GREEN;
                }
                renderer.render(g, brick, brickColor);
            }
        }
        for (var powerball : gameEngine.getPowerUps()) {
            Color powerballColor;
            PowerUpBall powerBall =  (PowerUpBall) powerball;
            if (powerBall.getPowerUpType().equals(PowerUpBall.PowerUpType.Fire_Ball)) {
                powerballColor = new Color(255, 60, 60);
            } else if (powerBall.getPowerUpType().equals(PowerUpBall.PowerUpType.Enlarged_Ball)) {
                powerballColor = new Color(255, 255, 255);
            } else  {
                powerballColor = new  Color(255, 0, 255);
            }
            renderer.render(g, powerball, powerballColor);
        }

        renderUI(g);
    }

    private void renderUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Score: " + gameEngine.getScore(), 10, 20);
        g.drawString("Lives: " + gameEngine.getLives(), 10, 40);

        int lineHeight = 50;
        if (gameEngine.isPaused()) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String pausedText = "Paused";
            int textWidth = g.getFontMetrics().stringWidth(pausedText);
            g.drawString(pausedText, (getWidth() - textWidth) / 2, getHeight() / 2);
        } else if (gameEngine.isGameOver()) {
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
            if (!gameEngine.getBall().isLaunched()) {
                // initial launch direction: slightly to the right and upward
                gameEngine.getBall().launch(0.2, -1.0);
            }
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