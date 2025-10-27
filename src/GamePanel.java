import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

        renderer.render(g, gameEngine.getPaddle(), Color.BLUE);
        renderer.render(g, gameEngine.getBall(), Color.RED);
        for (Brick brick : gameEngine.getBricks()) {
            if (!brick.isDestroyed()) {
                Color brickColor;
                if (brick instanceof StrongBrick) {
                    int hp = brick.getHitPoint();
                    if (hp >= 3) {
                        brickColor = new Color(139, 0, 0);
                    } else if (hp == 2) {
                        brickColor = new Color(178, 34, 34);
                    } else {
                        brickColor = new Color(205, 92, 92);
                    }
                } else {
                    brickColor = Color.green;
                }
                renderer.render(g, brick, brickColor);
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
        if (gameEngine.isPaused()) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String Paused = "Paused";
            int textWidth = g.getFontMetrics().stringWidth(Paused);
            g.drawString(Paused
                    , (getWidth() - textWidth) / 2
                    , getHeight() / 2);
        } else if (gameEngine.isGameOver()) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String GameOver = "Game Over";
            int textWidth = g.getFontMetrics().stringWidth(GameOver);
            g.drawString(GameOver
                    , (getWidth() - textWidth) / 2
                    , getHeight() / 2);

            String restartGame = "Press R to restart";
            int textWidth2 = g.getFontMetrics().stringWidth(restartGame);
            g.drawString(restartGame
                    , (getWidth() - textWidth2) / 2
                    , getHeight() / 2 + lineHeight);
        } else if (gameEngine.isGameWon()) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String GameWon = "Game Won";
            int textWidth = g.getFontMetrics().stringWidth(GameWon);
            g.drawString(GameWon
                    , (getWidth() - textWidth) / 2,
                    (getHeight() - textWidth) / 2);

            String restartGame = "Press R to restart";
            int textWidth2 = g.getFontMetrics().stringWidth(restartGame);
            g.drawString(restartGame
                    , (getWidth() - textWidth2) / 2
                    , getHeight() / 2 + lineHeight);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (pressedLeft && !pressedRight) {
            gameEngine.getPaddle().MoveLeft();
        } else if (pressedRight && !pressedLeft) {
            gameEngine.getPaddle().MoveRight();
        } else {
            gameEngine.getPaddle().Stop();
        }
        gameEngine.update();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            pressedLeft = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            pressedRight = true;
        }
        if (key == KeyEvent.VK_P) {
            gameEngine.TogglePaused();
        }
        if (key == KeyEvent.VK_R) {
            if (gameEngine.isGameOver()|| gameEngine.isGameWon()) {
                gameEngine.Restart();
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
