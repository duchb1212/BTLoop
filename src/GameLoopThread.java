// GameLoopThread.java

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLoopThread extends JPanel implements Runnable {
    private Thread gameThread;
    private boolean running;
    private final int targetFPS = 60;
    private final long optimalTime = 1000000000 / targetFPS;

    public GameLoopThread() {
        gameThread = new Thread(this);
        running = true;
    }

    public void start() {
        gameThread.start();
    }

    @Override
    public void run() {
        long lastLoopTime = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            long updateTime = now - lastLoopTime;
            lastLoopTime = now;

            // Calculate delta time
            double delta = updateTime / (double) optimalTime;

            // Update game logic
            updateGame(delta);

            // Render the game state
            repaint();

            // Sleep to maintain target FPS
            long sleepTime = (lastLoopTime - System.nanoTime() + optimalTime);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateGame(double delta) {
        // Implement game logic update here
    }

    public void stop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}